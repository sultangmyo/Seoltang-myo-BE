package com.seoltangmyo.sugarcat.domain.notification.service;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.seoltangmyo.sugarcat.global.config.apns.ApnsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApnsSender {

    private final ApnsClient apnsClient;
    private final ApnsProperties apnsProperties;
    private final ObjectMapper objectMapper;

    public boolean send(String deviceToken, Map<String, Object> payload) {
        try {
            String payloadJson = objectMapper.writeValueAsString(payload);

            SimpleApnsPushNotification notification =
                    new SimpleApnsPushNotification(
                            deviceToken,
                            apnsProperties.getBundleId(),
                            payloadJson
                    );

            PushNotificationResponse<SimpleApnsPushNotification> response =
                    apnsClient.sendNotification(notification).get();

            if (response.isAccepted()) {
                log.info("APNs 알림 전송 성공");
                return true;
            }

            log.warn("APNs 알림 전송 거부 - reason={}, tokenInvalidationTimestamp={}",
                    response.getRejectionReason(),
                    response.getTokenInvalidationTimestamp().orElse(null));

            return false;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("APNs 알림 전송 중 인터럽트 발생", e);
            return false;

        } catch (Exception e) {
            log.error("APNs 알림 전송 실패", e);
            return false;
        }
    }
}
