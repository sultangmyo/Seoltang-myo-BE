package com.seoltangmyo.sugarcat.domain.user.dto;

// PATCH /api/v1/users/me/notification?type={type} 요청 바디
// 알림 종류(type)별 개별 ON/OFF 전환
public record NotificationSingleUpdateRequest(
        boolean isEnabled
) {
}
