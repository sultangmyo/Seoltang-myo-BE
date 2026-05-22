package com.seoltangmyo.sugarcat.domain.notification.service.impl;

import com.seoltangmyo.sugarcat.domain.bloodsugar.entity.SugarStatus;
import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.notification.service.ApnsSender;
import com.seoltangmyo.sugarcat.domain.notification.service.NotificationPayloadFactory;
import com.seoltangmyo.sugarcat.domain.notification.service.NotificationService;
import com.seoltangmyo.sugarcat.domain.user.entity.User;
import com.seoltangmyo.sugarcat.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    @Transactional
    @Override
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

    @Transactional
    @Override
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

    @Transactional
    @Override
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

            boolean success = apnsSender.send(receiver.getApnsDeviceToken(), payload);

            if (!success) {
                receiver.deactivateApnsToken();
                log.warn("APNs 토큰 비활성화 - userId={}", receiver.getId());
            }
        }
    }
}
