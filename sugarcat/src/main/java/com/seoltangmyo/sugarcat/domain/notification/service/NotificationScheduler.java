package com.seoltangmyo.sugarcat.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationService notificationService;

    @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul")
    public void sendReminderNotifications() {
        log.info("리마인더 알림 스케줄러 실행");
        notificationService.sendDueReminderNotifications();
    }

    @Scheduled(cron = "0 0 9 * * MON", zone = "Asia/Seoul")
    public void sendWeeklyReportNotifications() {
        log.info("주간리포트 알림 스케줄러 실행");
        notificationService.sendWeeklyReportNotifications();
    }

}
