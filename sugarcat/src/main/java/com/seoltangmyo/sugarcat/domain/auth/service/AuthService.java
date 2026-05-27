package com.seoltangmyo.sugarcat.domain.auth.service;


import com.seoltangmyo.sugarcat.domain.auth.client.KakaoApiClient;
import com.seoltangmyo.sugarcat.domain.auth.dto.*;
import com.seoltangmyo.sugarcat.domain.user.entity.ProviderType;
import com.seoltangmyo.sugarcat.domain.user.entity.User;
import com.seoltangmyo.sugarcat.domain.user.repository.UserRepository;
import com.seoltangmyo.sugarcat.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final ProviderType APPLE = ProviderType.APPLE;
    private static final ProviderType KAKAO = ProviderType.KAKAO;

    private final UserRepository userRepository; // 유저 조회/저장용 리포지토리
    private final JwtProvider jwtProvider; // JWT 생성/검증 도구
    private final AppleTokenVerifier appleTokenVerifier;

    private final KakaoApiClient kakaoApiClient;

    @Transactional
    public SocialLoginResponse appleLogin(AppleLoginRequest request) {
        log.info("##log## 서비스 - 애플로그인 진입 ");
        String identityToken = request.identityToken(); // 프론트가 보낸 Apple identity token

        log.info("애플 토큰 null 여부 = {}", identityToken == null);
        log.info("애플 토큰 길이 = {}", identityToken == null ? null : identityToken.length());
        log.info("애플 토큰 앞부분 = {}",
                identityToken == null ? null : identityToken.substring(0, Math.min(10, identityToken.length())));

        log.info("##log## 서비스 - 애플 identityToken 검증 시작");

        // Apple JWKS 검증 후 sub 추출
        String providerId = extractAppleProviderId(identityToken); // Apple의 고유 식별값(sub)

        log.info("##log## 서비스 - 애플 identityToken 검증 성공");
        log.info("애플 providerId 추출 성공 여부 = {}", providerId != null);
        log.info("애플 providerId 길이 = {}", providerId == null ? null : providerId.length());

        SocialLoginResponse response = loginOrSignUp(APPLE, providerId);

        log.info("##log## 서비스 - 애플로그인 종료 ");

        return response;
    }

    @Transactional
    public SocialLoginResponse kakaoLogin(KakaoLoginRequest request) {
        log.info("##log## 서비스 - 카카오로그인 진입 ");
        String kakaoAccessToken = request.accessToken(); // 프론트가 보낸 카카오 access token

        log.info("카카오 토큰 null 여부 = {}", kakaoAccessToken == null);
        log.info("카카오 토큰 길이 = {}", kakaoAccessToken == null ? null : kakaoAccessToken.length());
        log.info("카카오 토큰 앞부분 = {}", kakaoAccessToken == null ? null : kakaoAccessToken.substring(0, Math.min(10, kakaoAccessToken.length())));

        // 카카오 API 호출 후 user id 추출
        String providerId = extractKakaoProviderId(kakaoAccessToken); // 카카오의 고유 식별값(id)

        log.info("##log## 서비스 - 카카오로그인 종료 ");
        return loginOrSignUp(KAKAO, providerId); // 기존 유저 조회 또는 신규 판단 후 토큰 발급
    }

    @Transactional
    public TokenRefreshResponse refresh(String refreshToken) {
        if (!jwtProvider.validate(refreshToken)) { // 토큰 형식/서명/만료 검증
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        if (!"REFRESH".equals(jwtProvider.getType(refreshToken))) { // Refresh Token인지 확인
            throw new IllegalArgumentException("Refresh Token이 아닙니다.");
        }

        UUID userId = jwtProvider.getUserId(refreshToken); // 토큰에서 userId 추출

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getRefreshToken() == null) { // 서버에 저장된 refresh token이 없으면 로그아웃 상태로 판단
            throw new IllegalArgumentException("저장된 Refresh Token이 없습니다.");
        }

        if (!user.getRefreshToken().equals(refreshToken)) { // 프론트가 보낸 토큰과 DB 저장값 비교
            throw new IllegalArgumentException("Refresh Token이 일치하지 않습니다.");
        }

        if (user.getRefreshTokenExpiresAt() == null || user.getRefreshTokenExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Refresh Token이 만료되었습니다.");
        }

        String newAccessToken = jwtProvider.createAccessToken(user.getId()); // 새 Access Token 발급

        return new TokenRefreshResponse(newAccessToken, user.getId()); // 명세에 맞게 accessToken + userId 반환
    }

    @Transactional
    public void logout(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.clearRefreshToken(); // DB에 저장된 refresh token 제거
        user.deactivateApnsToken(); // device token 비활성화
    }

    public OnboardingStatusResponse getOnboardingStatus(UUID userId) {
        log.info("##log## 서비스 - 온보딩 진입 ");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        log.info("##log## 서비스 - 온보딩 종료 유저 아이디: {}", userId);
        return new OnboardingStatusResponse(user.isOnboardingCompleted());
    }

    @Transactional
    public OnboardingCompleteResponse completeOnboarding(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.completeOnboarding();

        // 명세: { "message": "온보딩이 완료되었습니다.", "onboardingCompleted": true }
        return new OnboardingCompleteResponse("온보딩이 완료되었습니다.", true);
    }
  
    private SocialLoginResponse loginOrSignUp(ProviderType provider, String providerId) {
        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(()->userRepository.save(User.createSocialUser(provider, providerId)));

        String accessToken = jwtProvider.createAccessToken(user.getId());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());
        Instant refreshTokenExpiresAt = jwtProvider.getExpiration(refreshToken);

        user.updateRefreshToken(refreshToken, refreshTokenExpiresAt);

        return new SocialLoginResponse(accessToken, refreshToken);
    }

    private String extractAppleProviderId(String identityToken) {
        return appleTokenVerifier.verifyAndExtractSubject(identityToken);
    }

    private String extractKakaoProviderId(String kakaoAccessToken) {
        KakaoUserInfoResponse response = kakaoApiClient.getUserInfo(kakaoAccessToken); // 카카오 서버에 사용자 정보 요청

        Long id = response.id();

        if (id == null) {
            throw new IllegalArgumentException("카카오 사용자 정보를 가져오지 못했습니다.");
        }

        return id.toString();
    }
}