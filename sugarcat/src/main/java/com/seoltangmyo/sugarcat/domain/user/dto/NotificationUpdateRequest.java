package com.seoltangmyo.sugarcat.domain.user.dto;

// PATCH /api/v1/users/me/notification 요청 바디
// true  → 인슐린/혈당/식사/주간리포트 알림 전체 ON
// false → 전체 OFF
public record NotificationUpdateRequest(
        boolean notificationEnabled  // 전체 알림 허용 여부
) {
}
