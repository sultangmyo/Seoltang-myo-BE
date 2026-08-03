package com.seoltangmyo.sugarcat.domain.notification.service.impl;

import com.seoltangmyo.sugarcat.domain.bloodsugar.entity.SugarStatus;
import com.seoltangmyo.sugarcat.domain.bloodsugar.repository.BloodSugarRecordRepository;
import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.cat.repository.CatRepository;
import com.seoltangmyo.sugarcat.domain.insulin.repository.InsulinRecordRepository;
import com.seoltangmyo.sugarcat.domain.meal.repository.MealRecordRepository;
import com.seoltangmyo.sugarcat.domain.notification.dto.ApnsSendResult;
import com.seoltangmyo.sugarcat.domain.notification.service.ApnsSender;
import com.seoltangmyo.sugarcat.domain.notification.service.NotificationPayloadFactory;
import com.seoltangmyo.sugarcat.domain.notification.service.NotificationService;
import com.seoltangmyo.sugarcat.domain.notification.service.PushTokenService;
import com.seoltangmyo.sugarcat.domain.schedule.entity.CareSchedule;
import com.seoltangmyo.sugarcat.domain.schedule.repository.CareScheduleRepository;
import com.seoltangmyo.sugarcat.domain.user.entity.User;
import com.seoltangmyo.sugarcat.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

    private final UserRepository userRepository;
    private final ApnsSender apnsSender;
    private final NotificationPayloadFactory payloadFactory;
    private final CareScheduleRepository careScheduleRepository;
    private final InsulinRecordRepository insulinRecordRepository;
    private final BloodSugarRecordRepository bloodSugarRecordRepository;
    private final MealRecordRepository mealRecordRepository;
    private final CatRepository catRepository;
    private final PushTokenService pushTokenService;

    @Override
    @Transactional
    public void sendInsulinCompletedToFamily(
            Cat cat,
            String actorNickname,
            LocalDate targetDate,
            int sequence
    ) {
        Map<String, Object> payload = payloadFactory.createInsulinCompletedPayload(
                actorNickname,
                targetDate,
                sequence
        );

        sendToFamily(
                cat,
                User::canReceiveInsulinNotification,
                payload
        );
    }

    @Override
    @Transactional
    public void sendMealCompletedToFamily(
            Cat cat,
            String actorNickname,
            LocalDate targetDate,
            int sequence
    ) {
        Map<String, Object> payload = payloadFactory.createMealCompletedPayload(
                actorNickname,
                targetDate,
                sequence
        );

        sendToFamily(
                cat,
                User::canReceiveMealNotification,
                payload
        );
    }

    @Override
    @Transactional
    public void sendBloodSugarCompletedToFamily(
            Cat cat,
            String actorNickname,
            int sugarValue,
            SugarStatus sugarStatus,
            LocalDate targetDate,
            int sequence
    ) {
        Map<String, Object> payload = payloadFactory.createBloodSugarCompletedPayload(
                actorNickname,
                sugarValue,
                sugarStatus,
                targetDate,
                sequence
        );

        sendToFamily(
                cat,
                User::canReceiveBloodSugarNotification,
                payload
        );
    }

    @Override
    @Transactional
    public void sendDueReminderNotifications() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        LocalTime now = LocalTime.now(ZoneId.of("Asia/Seoul"))
                .withSecond(0)
                .withNano(0);

        List<CareSchedule> schedules = careScheduleRepository.findAllByScheduledTime(now);

        for (CareSchedule schedule : schedules) {
            sendReminderIfNotRecorded(schedule, today);
        }

    }

    @Override
    @Transactional
    public void sendWeeklyReportNotifications() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate thisMonday = today.with(DayOfWeek.MONDAY);
        LocalDate lastMonday = thisMonday.minusWeeks(1);
        LocalDate lastSunday = thisMonday.minusDays(1);

        List<Cat> cats = catRepository.findAll();

        for (Cat cat : cats) {
            sendWeeklyReportToFamily(cat, lastMonday, lastSunday);
        }
    }

    private void sendReminderIfNotRecorded(CareSchedule schedule, LocalDate today) {
        switch (schedule.getScheduleType()) {
            case INSULIN -> sendInsulinReminderIfNotRecorded(schedule, today);
            case BLOODSUGAR -> sendBloodSugarReminderIfNotRecorded(schedule, today);
            case MEAL -> sendMealReminderIfNotRecorded(schedule, today);
        }
    }

    private void sendInsulinReminderIfNotRecorded(CareSchedule schedule, LocalDate today) {
        boolean alreadyRecorded = insulinRecordRepository.existsByCatAndRecordDateAndSequence(
                schedule.getCat(),
                today,
                schedule.getSequence()
        );

        if (alreadyRecorded) {
            return;
        }

        Map<String, Object> payload = payloadFactory.createInsulinReminderPayload(
                today,
                schedule.getSequence()
        );

        sendToFamily(
                schedule.getCat(),
                User::canReceiveInsulinNotification,
                payload
        );
    }

    private void sendBloodSugarReminderIfNotRecorded(CareSchedule schedule, LocalDate today) {
        boolean alreadyRecorded = bloodSugarRecordRepository.existsByCatAndRecordDateAndSequence(
                schedule.getCat(),
                today,
                schedule.getSequence()
        );

        if (alreadyRecorded) {
            return;
        }

        Map<String, Object> payload = payloadFactory.createBloodSugarReminderPayload(
                today,
                schedule.getSequence()
        );

        sendToFamily(
                schedule.getCat(),
                User::canReceiveBloodSugarNotification,
                payload
        );
    }

    private void sendMealReminderIfNotRecorded(CareSchedule schedule, LocalDate today) {
        boolean alreadyRecorded = mealRecordRepository.existsByCatAndRecordDateAndSequence(
                schedule.getCat(),
                today,
                schedule.getSequence()
        );

        if (alreadyRecorded) {
            return;
        }

        Map<String, Object> payload = payloadFactory.createMealReminderPayload(
                today,
                schedule.getSequence()
        );

        sendToFamily(
                schedule.getCat(),
                User::canReceiveMealNotification,
                payload
        );
    }

    private void sendWeeklyReportToFamily(Cat cat, LocalDate startDate, LocalDate endDate) {
        long totalCount = bloodSugarRecordRepository.countByCatAndRecordDateBetween(cat, startDate, endDate);

        long lowCount = bloodSugarRecordRepository.countByCatAndRecordDateBetweenAndSugarStatus(
                cat,
                startDate,
                endDate,
                SugarStatus.LOW
        );

        long highCount = bloodSugarRecordRepository.countByCatAndRecordDateBetweenAndSugarStatus(
                cat,
                startDate,
                endDate,
                SugarStatus.HIGH
        );

        String body = createWeeklyReportBody(cat.getName(), totalCount, lowCount, highCount);

        Map<String, Object> payload = payloadFactory.createWeeklyReportPayload(body);

        sendToFamily(
                cat,
                User::canReceiveWeeklyReportNotification,
                payload
        );
    }

    private String createWeeklyReportBody(String catName, long totalCount, long lowCount, long highCount) {
        // 기록이 하나도 없는 경우
        if (totalCount == 0) {
            return catName + "는 이번 주 어떻게 지냈나요? 지금 기록하고 다음 주 건강 리포트를 받아보세요.";
        }

        // 고혈당/저혈당이 하나도 없는 경우
        if (lowCount == 0 && highCount == 0) {
            return "저번 주는 고혈당·저혈당 없이 모두 정상 범위였어요 🎉";
        }

        // 고혈당/저혈당 기록이 있는 경우
        return "저번 주 저혈당은 " + lowCount + "번, 고혈당은 " + highCount + "번 있었어요.";
    }


    private void sendToFamily(
            Cat cat,
            Predicate<User> canReceive,
            Map<String, Object> payload
    ) {
        List<User> familyUsers = userRepository.findAllByCat(cat);

        for(User receiver : familyUsers) {
            if (!canReceive.test(receiver)) {
                log.debug("알림 발송 스킵 - userId={}", receiver.getId());
                continue;
            }

            ApnsSendResult result = apnsSender.send(receiver.getApnsDeviceToken(), payload);

            if (result.success()) {
                log.info("APNs 전송 성공 - userId={}", receiver.getId());
                continue;
            }

            if (result.invalidToken()) {
                pushTokenService.deactivateApnsToken(receiver.getId());
                log.warn("APNs 토큰 비활성화 - userId={}, reason={}",
                        receiver.getId(),
                        result.reason());
                continue;
            }

            log.warn("APNs 전송 실패, 토큰은 유지 - userId={}, reason={}",
                    receiver.getId(),
                    result.reason());
        }
    }
}
