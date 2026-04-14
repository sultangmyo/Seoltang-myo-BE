package com.seoltangmyo.sugarcat.domain.auth.service;


import com.seoltangmyo.sugarcat.domain.auth.dto.AppleLoginRequest;
import com.seoltangmyo.sugarcat.domain.auth.dto.KakaoLoginRequest;
import com.seoltangmyo.sugarcat.domain.auth.dto.SocialLoginResponse;
import com.seoltangmyo.sugarcat.domain.auth.dto.TokenRefreshResponse;
import com.seoltangmyo.sugarcat.domain.user.entity.ProviderType;
import com.seoltangmyo.sugarcat.domain.user.entity.User;
import com.seoltangmyo.sugarcat.domain.user.repository.UserRepository;
import com.seoltangmyo.sugarcat.global.security.jwt.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final ProviderType APPLE = ProviderType.APPLE;
    private static final ProviderType KAKAO = ProviderType.KAKAO;

    private final UserRepository userRepository; // 유저 조회/저장용 리포지토리
    private final JwtProvider jwtProvider; // JWT 생성/검증 도구

    @Transactional
    public SocialLoginResponse appleLogin(AppleLoginRequest request) {
        String identityToken = request.identityToken(); // 프론트가 보낸 Apple identity token

        // Apple JWKS 검증 후 sub 추출
        String providerId = extractAppleProviderId(identityToken); // Apple의 고유 식별값(sub)

        return loginOrSignUp(APPLE, providerId); // 기존 유저 조회 또는 신규 판단 후 토큰 발급
    }

    @Transactional
    public SocialLoginResponse kakaoLogin(KakaoLoginRequest request) {
        String kakaoAccessToken = request.accessToken(); // 프론트가 보낸 카카오 access token

        // 카카오 API 호출 후 user id 추출
        String providerId = extractKakaoProviderId(kakaoAccessToken); // 카카오의 고유 식별값(id)

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

        if (user.getRefreshTokenExpiresAt() == null || user.getRefreshTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Refresh Token이 만료되었습니다.");
        }

        String newAccessToken = jwtProvider.createAccessToken(user.getUserId()); // 새 Access Token 발급

        return new TokenRefreshResponse(newAccessToken, user.getUserId()); // 명세에 맞게 accessToken + userId 반환
    }

    @Transactional
    public void logout(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.clearRefreshToken(); // DB에 저장된 refresh token 제거
    }

    @Transactional
    protected SocialLoginResponse loginOrSignUp(ProviderType provider, String providerId) {
        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElse(null); // provider + providerId 조합으로 기존 유저 조회

        boolean isNewUser = (user == null); // 신규 유저 여부 판단

        if (isNewUser) {
            // 아직 너희 플로우상 신규 유저는 "고양이 생성 후 유저 생성"이므로
            // 여기서는 회원 생성까지 하지 않고, 신규 여부만 내려주는 방식도 가능함.
            // 하지만 지금 명세상 accessToken/refreshToken을 내려주고 있어서,
            // 실제로는 유저가 이미 생성된 뒤 이 API가 호출되도록 맞추거나,
            // 아래 로직을 너희 생성 플로우에 맞게 바꿔야 함.
            throw new IllegalStateException("신규 유저입니다. 먼저 고양이/유저 생성 플로우를 완료해야 합니다.");
        }

        String accessToken = jwtProvider.createAccessToken(user.getUserId()); // Access Token 발급
        String refreshToken = jwtProvider.createRefreshToken(user.getUserId()); // Refresh Token 발급
        LocalDateTime refreshTokenExpiresAt = jwtProvider.getExpiration(refreshToken); // Refresh Token 만료 시각 계산

        user.updateRefreshToken(refreshToken, refreshTokenExpiresAt); // DB에 Refresh Token 저장

        return new SocialLoginResponse(accessToken, refreshToken, false); // 기존 유저 로그인 응답
    }

    private String extractAppleProviderId(String identityToken) {
        // Apple identity token 검증 후 sub 반환
        // 지금은 임시 예시
        throw new UnsupportedOperationException("Apple 로그인 검증 로직을 아직 구현하지 않았습니다.");
    }

    private String extractKakaoProviderId(String kakaoAccessToken) {
        // 카카오 사용자 정보 API 호출 후 id 반환
        // 지금은 임시 예시
        throw new UnsupportedOperationException("Kakao 로그인 검증 로직을 아직 구현하지 않았습니다.");
    }
}