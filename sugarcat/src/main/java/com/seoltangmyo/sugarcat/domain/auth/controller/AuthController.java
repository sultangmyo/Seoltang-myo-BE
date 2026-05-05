package com.seoltangmyo.sugarcat.domain.auth.controller;

import com.seoltangmyo.sugarcat.domain.auth.dto.AppleLoginRequest;
import com.seoltangmyo.sugarcat.domain.auth.dto.KakaoLoginRequest;
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
        SocialLoginResponse response = authService.appleLogin(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/kakao")
    public ResponseEntity<SocialLoginResponse> kakaoLogin(
            @RequestBody KakaoLoginRequest request
    ) {
        SocialLoginResponse response = authService.kakaoLogin(request);
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
        UUID userId = userDetails.getUserId();
        OnboardingStatusResponse response = authService.getOnboardingStatus(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/onboarding")
    public ResponseEntity<Void> completeOnboarding(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        authService.completeOnboarding(userId);
        return ResponseEntity.noContent().build();
    }
}