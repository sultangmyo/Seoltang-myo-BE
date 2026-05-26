package com.seoltangmyo.sugarcat.domain.user.dto;

// 단순 성공 메시지 응답 공용 DTO
// 닉네임 수정, 알림 수정 등 결과 본문이 메시지 하나인 경우 사용
public record MessageResponse(
        String message
) {
}
