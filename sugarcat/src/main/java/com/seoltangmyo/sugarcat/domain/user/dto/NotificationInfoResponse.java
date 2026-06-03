package com.seoltangmyo.sugarcat.domain.user.dto;

// GET /api/v1/users/me/notification 응답
// 사용자의 4종류 알림 설정 상태 반환
public record NotificationInfoResponse(
        boolean insulinNotificationEnabled,
        boolean bloodSugarNotificationEnabled,
        boolean mealNotificationEnabled,
        boolean weeklyReportNotificationEnabled
) {
}
