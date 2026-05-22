package com.seoltangmyo.sugarcat.domain.notification.type;

public enum NotificationType {

    // 스케줄러 알림
    INSULIN_REMINDER,
    BLOOD_SUGAR_REMINDER,
    MEAL_REMINDER,
    WEEKLY_REPORT,

    // 이벤트 알림
    INSULIN_COMPLETED,
    BLOOD_SUGAR_COMPLETED,
    MEAL_COMPLETED,
}
