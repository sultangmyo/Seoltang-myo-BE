package com.seoltangmyo.sugarcat.domain.notification.service;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.seoltangmyo.sugarcat.domain.notification.dto.ApnsSendResult;
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

    public ApnsSendResult send(String deviceToken, Map<String, Object> payload) {
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
                return ApnsSendResult.accepted();
            }

            String reason = response.getRejectionReason()
                    .orElse("UNKNOWN");
            boolean invalidToken = isInvalidTokenReason(reason);

            log.warn(
                    "APNs 전송 거부 - reason={}, invalidToken={}, invalidatedAt={}",
                    reason,
                    invalidToken,
                    response.getTokenInvalidationTimestamp().orElse(null)
            );

            return ApnsSendResult.rejected(reason, invalidToken);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("APNs 알림 전송 중 인터럽트 발생", e);
            return ApnsSendResult.failed("INTERRUPTED");

        } catch (Exception e) {
            log.error("APNs 알림 전송 실패", e);
            return ApnsSendResult.failed("SEND_FAILED");
        }
    }

    // 토큰 자체가 잘못된 경우만 true
    private boolean isInvalidTokenReason(String reason) {
        return "BadDeviceToken".equals(reason)
                || "Unregistered".equals(reason)
                || "DeviceTokenNotForTopic".equals(reason);
    }
}
