package com.seoltangmyo.sugarcat.domain.auth.controller;

import com.seoltangmyo.sugarcat.domain.auth.dto.AppleLoginRequest;
import com.seoltangmyo.sugarcat.domain.auth.dto.KakaoLoginRequest;
import com.seoltangmyo.sugarcat.domain.auth.dto.OnboardingCompleteResponse;
import com.seoltangmyo.sugarcat.domain.auth.dto.OnboardingStatusResponse;
import com.seoltangmyo.sugarcat.domain.auth.dto.SocialLoginResponse;
import com.seoltangmyo.sugarcat.domain.auth.dto.TokenRefreshResponse;
import com.seoltangmyo.sugarcat.domain.auth.service.AuthService;
import com.seoltangmyo.sugarcat.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/apple")
    public ResponseEntity<SocialLoginResponse> appleLogin(
            @RequestBody AppleLoginRequest request
    ) {
        log.info("body = {}", request);
        log.info("##log## 컨트롤러 - 애플로그인 진입 ");
        SocialLoginResponse response = authService.appleLogin(request);
        log.info("##log## 컨트롤러 - 애플로그인 종료");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/kakao")
    public ResponseEntity<SocialLoginResponse> kakaoLogin(
            @RequestBody KakaoLoginRequest request
    ) {
        log.info("body = {}", request);
        log.info("##log## 컨트롤러 - 카카오로그인 진입 ");
        SocialLoginResponse response = authService.kakaoLogin(request);
        log.info("##log## 컨트롤러 - 카카오로그인 종료");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refresh(
            @RequestHeader("Refresh-Token") String refreshToken
    ) {
        TokenRefreshResponse response = authService.refresh(refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        authService.logout(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/onboarding")
    public ResponseEntity<OnboardingStatusResponse> getOnboardingStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("##log## 컨트롤러 - 온보딩 진입 ");
        UUID userId = userDetails.getUserId();
        OnboardingStatusResponse response = authService.getOnboardingStatus(userId);
        log.info("##log## 컨트롤러 - 온보딩 종료 ");
        return ResponseEntity.ok(response);
    }

    // 온보딩 완료 저장
    // POST /api/v1/auth/onboarding
    // 온보딩 마지막 단계: onboardingCompleted = true 로 저장 후 홈 화면 이동
    @PostMapping("/onboarding")
    public ResponseEntity<OnboardingCompleteResponse> completeOnboarding(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        OnboardingCompleteResponse response = authService.completeOnboarding(userId);
        return ResponseEntity.ok(response);
    }
}