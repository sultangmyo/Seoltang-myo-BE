package com.seoltangmyo.sugarcat.domain.auth.controller;

import com.seoltangmyo.sugarcat.domain.auth.dto.AppleLoginRequest;
import com.seoltangmyo.sugarcat.domain.auth.dto.KakaoLoginRequest;
import com.seoltangmyo.sugarcat.domain.auth.dto.OnboardingCompleteResponse;
import com.seoltangmyo.sugarcat.domain.auth.dto.OnboardingStatusResponse;
import com.seoltangmyo.sugarcat.domain.auth.dto.SocialLoginResponse;
import com.seoltangmyo.sugarcat.domain.auth.dto.TokenRefreshResponse;
import com.seoltangmyo.sugarcat.domain.auth.service.AuthService;
import com.seoltangmyo.sugarcat.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Auth", description = "인증/온보딩 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "애플 로그인")
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

    @Operation(summary = "카카오 로그인")
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

    @Operation(summary = "토큰 재발급")
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refresh(
            @RequestHeader("Refresh-Token") String refreshToken
    ) {
        TokenRefreshResponse response = authService.refresh(refreshToken);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        authService.logout(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "온보딩 완료 여부 조회")
    @GetMapping("/onboarding")
    public ResponseEntity<OnboardingStatusResponse> getOnboardingStatus(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
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
    @Operation(summary = "온보딩 완료 저장")
    @PostMapping("/onboarding")
    public ResponseEntity<OnboardingCompleteResponse> completeOnboarding(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        OnboardingCompleteResponse response = authService.completeOnboarding(userId);
        return ResponseEntity.ok(response);
    }
}