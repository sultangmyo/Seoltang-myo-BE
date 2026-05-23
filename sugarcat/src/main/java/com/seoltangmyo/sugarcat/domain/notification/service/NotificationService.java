package com.seoltangmyo.sugarcat.domain.notification.service;

import com.seoltangmyo.sugarcat.domain.bloodsugar.entity.SugarStatus;
import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;

import java.time.LocalDate;

public interface NotificationService {

    void sendInsulinCompletedToFamily(
            Cat cat,
            String actorNickname,
            LocalDate targetDate,
            int sequence
    );

    void sendMealCompletedToFamily(
            Cat cat,
            String actorNickname,
            LocalDate targetDate,
            int sequence
    );

    void sendBloodSugarCompletedToFamily(
            Cat cat,
            String actorNickname,
            int sugarValue,
            SugarStatus sugarStatus,
            LocalDate targetDate,
            int sequence
    );

    void sendDueReminderNotifications();

    void sendWeeklyReportNotifications();
}
