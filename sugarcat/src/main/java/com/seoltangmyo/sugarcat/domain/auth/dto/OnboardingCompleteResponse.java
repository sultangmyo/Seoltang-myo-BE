package com.seoltangmyo.sugarcat.domain.auth.dto;

// POST /api/v1/auth/onboarding 응답 바디
// 온보딩 완료 저장 성공 시 메시지와 완료 여부 함께 반환
public record OnboardingCompleteResponse(
        String message,
        boolean onboardingCompleted  // 항상 true (완료 처리된 결과값)
) {
}
