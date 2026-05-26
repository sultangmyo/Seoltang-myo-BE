package com.seoltangmyo.sugarcat.domain.notification.service;

import com.seoltangmyo.sugarcat.domain.bloodsugar.entity.SugarStatus;
import com.seoltangmyo.sugarcat.domain.notification.type.NotificationType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
public class NotificationPayloadFactory {

    public Map<String, Object> createInsulinReminderPayload(
            LocalDate targetDate,
            int sequence
    ) {
        return createPayload(
                "💊 인슐린 투여 시간",
                "지금 인슐린을 투여해주세요.",
                NotificationType.INSULIN_REMINDER,
                targetDate,
                sequence
        );
    }

    public Map<String, Object> createInsulinCompletedPayload(
            String nickname,
            LocalDate targetDate,
            int sequence
    ) {
        return createPayload(
                "✅ 인슐린 투여 완료",
                nickname + "님이 인슐린 투여를 완료했어요.",
                NotificationType.INSULIN_COMPLETED,
                targetDate,
                sequence
        );
    }

    public Map<String, Object> createBloodSugarReminderPayload(LocalDate targetDate, int sequence) {

        return createPayload(
                "🩸 혈당 기록 시간",
                "지금 혈당을 측정하고 기록해주세요.",
                NotificationType.BLOOD_SUGAR_REMINDER,
                targetDate,
                sequence
        );
    }

    public Map<String, Object> createBloodSugarCompletedPayload(
            String nickname,
            int sugarValue,
            SugarStatus sugarStatus,
            LocalDate targetDate,
            int sequence
    ) {
        return createPayload(
                "✅ 혈당 기록 완료",
                nickname + "님이 혈당을 기록했어요. "
                        + sugarValue + "mg/dL · "
                        + toSugarStatusText(sugarStatus) + " "
                        + toSugarStatusEmoji(sugarStatus),
                NotificationType.BLOOD_SUGAR_COMPLETED,
                targetDate,
                sequence
        );
    }

    public Map<String, Object> createMealReminderPayload(
            LocalDate targetDate,
            int sequence
    ) {
        return createPayload(
                "🍚 식사 기록 시간",
                "식사 후 남은 양을 기록해주세요.",
                NotificationType.MEAL_REMINDER,
                targetDate,
                sequence
        );
    }

    public Map<String, Object> createMealCompletedPayload(
            String nickname,
            LocalDate targetDate,
            int sequence
    ) {
        return createPayload(
                "✅ 식사 기록 완료",
                nickname + "님이 식사를 기록했어요.",
                NotificationType.MEAL_COMPLETED,
                targetDate,
                sequence
        );
    }

    public Map<String, Object> createWeeklyReportPayload(String body) {
        Map<String, Object> alert = new HashMap<>();
        alert.put("title", "📅 주간리포트");
        alert.put("body", body);

        Map<String, Object> aps = new HashMap<>();
        aps.put("alert", alert);
        aps.put("sound", "default");

        Map<String, Object> payload = new HashMap<>();
        payload.put("aps", aps);
        payload.put("notificationType", NotificationType.WEEKLY_REPORT.name());

        return payload;
    }

    private Map<String, Object> createPayload(
            String title,
            String body,
            NotificationType notificationType,
            LocalDate targetDate,
            int sequence
    ) {
        Map<String, Object> alert = new HashMap<>();
        alert.put("title", title);
        alert.put("body", body);

        Map<String, Object> aps = new HashMap<>();
        aps.put("alert", alert);
        aps.put("sound", "default");

        Map<String, Object> payload = new HashMap<>();
        payload.put("aps", aps);
        payload.put("notificationType", notificationType.name());
        payload.put("targetDate", targetDate.toString());
        payload.put("sequence", sequence);

        return payload;
    }

    private String toSugarStatusText(SugarStatus status) {
        return switch (status) {
            case NORMAL -> "정상";
            case HIGH -> "높음";
            case LOW -> "낮음";
        };
    }

    private String toSugarStatusEmoji(SugarStatus status) {
        return switch (status) {
            case NORMAL -> "🟢";
            case HIGH -> "🟡";
            case LOW -> "🔵";
        };
    }
}
