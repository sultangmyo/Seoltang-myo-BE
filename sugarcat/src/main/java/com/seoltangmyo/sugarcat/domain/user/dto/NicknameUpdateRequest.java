package com.seoltangmyo.sugarcat.domain.user.dto;

// PATCH /api/v1/users/me/nickname 요청 바디
// 유효성 검증(한글/영문 2~6자)은 프론트에서 처리 → 백엔드는 null/빈값만 체크
public record NicknameUpdateRequest(
        String nickname  // 변경할 닉네임
) {
}
