package com.seoltangmyo.sugarcat.domain.user.dto;

// PATCH /api/v1/users/me/cat 요청 바디
// 초대코드로 고양이 검증 + 사용자 catId 저장을 한 번에 처리
// UUID 대신 inviteCode를 식별자로 사용하여 UUID 노출 없이 합류 처리
public record CatUpdateRequest(
        String inviteCode  // 8자리 초대코드
) {
}
