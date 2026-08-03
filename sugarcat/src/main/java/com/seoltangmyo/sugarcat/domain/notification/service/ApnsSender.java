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
            // Map 형태의 payload를 APNs에 보낼 수 있는 JSON 문자열로 변환한다.
            String payloadJson = objectMapper.writeValueAsString(payload);

            // APNs 전송 직전에 실제로 사용할 설정값과 토큰 형태를 확인한다.
            // 토큰 전체는 민감할 수 있으므로 앞/뒤 일부만 로그로 남긴다.
            log.info(
                    "[APNs 전송 준비] production={}, topic={}, tokenLength={}, tokenPrefix={}, tokenSuffix={}, containsSpace={}, containsAngleBracket={}, payload={}",
                    apnsProperties.isProduction(),
                    apnsProperties.getBundleId(),
                    getTokenLength(deviceToken),
                    getTokenPrefix(deviceToken),
                    getTokenSuffix(deviceToken),
                    containsSpace(deviceToken),
                    containsAngleBracket(deviceToken),
                    payloadJson
            );

            // APNs 알림 객체를 생성한다.
            // 두 번째 인자인 topic은 iOS 앱의 Bundle Identifier와 정확히 같아야 한다.
            SimpleApnsPushNotification notification =
                    new SimpleApnsPushNotification(
                            deviceToken,
                            apnsProperties.getBundleId(),
                            payloadJson
                    );

            // 실제 생성된 notification에서 토큰과 topic이 예상대로 들어갔는지 한 번 더 확인한다.
            log.info(
                    "[APNs 알림 객체 생성 완료] topic={}, tokenLength={}, tokenPrefix={}, tokenSuffix={}",
                    notification.getTopic(),
                    getTokenLength(notification.getToken()),
                    getTokenPrefix(notification.getToken()),
                    getTokenSuffix(notification.getToken())
            );

            // APNs 서버로 알림을 전송하고 응답을 기다린다.
            PushNotificationResponse<SimpleApnsPushNotification> response =
                    apnsClient.sendNotification(notification).get();

            // APNs가 알림을 정상 수락한 경우다.
            if (response.isAccepted()) {
                log.info(
                        "[APNs 전송 성공] topic={}, tokenPrefix={}, tokenSuffix={}",
                        notification.getTopic(),
                        getTokenPrefix(notification.getToken()),
                        getTokenSuffix(notification.getToken())
                );

                return ApnsSendResult.accepted();
            }

            // APNs가 알림을 거부한 경우, 거부 사유를 꺼낸다.
            String reason = response.getRejectionReason()
                    .orElse("UNKNOWN");

            // 이 reason이 토큰 자체를 비활성화해야 하는 사유인지 판단한다.
            boolean invalidToken = isInvalidTokenReason(reason);

            // APNs 거부 응답을 자세히 남긴다.
            log.warn(
                    "[APNs 전송 거부] reason={}, invalidToken={}, invalidatedAt={}, topic={}, tokenLength={}, tokenPrefix={}, tokenSuffix={}",
                    reason,
                    invalidToken,
                    response.getTokenInvalidationTimestamp().orElse(null),
                    notification.getTopic(),
                    getTokenLength(notification.getToken()),
                    getTokenPrefix(notification.getToken()),
                    getTokenSuffix(notification.getToken())
            );

            return ApnsSendResult.rejected(reason, invalidToken);

        } catch (InterruptedException e) {
            // interrupt가 발생하면 현재 스레드의 interrupt 상태를 복구한다.
            Thread.currentThread().interrupt();

            log.error("APNs 알림 전송 중 인터럽트 발생", e);

            return ApnsSendResult.failed("INTERRUPTED");

        } catch (Exception e) {
            // 네트워크 실패, DNS 실패, APNs 연결 실패, JSON 변환 실패 등이 여기로 들어온다.
            log.error(
                    "[APNs 전송 실패] production={}, topic={}, tokenLength={}, tokenPrefix={}, tokenSuffix={}",
                    apnsProperties.isProduction(),
                    apnsProperties.getBundleId(),
                    getTokenLength(deviceToken),
                    getTokenPrefix(deviceToken),
                    getTokenSuffix(deviceToken),
                    e
            );

            return ApnsSendResult.failed("SEND_FAILED");
        }
    }

    // 토큰 자체가 잘못된 경우만 true로 본다.
    private boolean isInvalidTokenReason(String reason) {
        return "BadDeviceToken".equals(reason)
                || "Unregistered".equals(reason)
                || "DeviceTokenNotForTopic".equals(reason);
    }

    // 토큰 길이를 반환한다.
    private int getTokenLength(String token) {
        // token이 null이면 길이를 0으로 본다.
        if (token == null) {
            return 0;
        }

        // null이 아니면 실제 문자열 길이를 반환한다.
        return token.length();
    }

    // 토큰 앞부분만 반환한다.
    private String getTokenPrefix(String token) {
        // token이 null이면 null을 반환한다.
        if (token == null) {
            return null;
        }

        // 최대 앞 12글자까지만 보여준다.
        return token.substring(0, Math.min(12, token.length()));
    }

    // 토큰 뒷부분만 반환한다.
    private String getTokenSuffix(String token) {
        // token이 null이면 null을 반환한다.
        if (token == null) {
            return null;
        }

        // 최대 뒤 12글자까지만 보여준다.
        return token.substring(Math.max(0, token.length() - 12));
    }

    // 토큰에 공백이 포함되어 있는지 확인한다.
    private boolean containsSpace(String token) {
        // token이 null이면 공백이 없다고 본다.
        if (token == null) {
            return false;
        }

        // 공백 문자가 포함되어 있는지 확인한다.
        return token.contains(" ");
    }

    // 토큰에 < 또는 > 문자가 포함되어 있는지 확인한다.
    private boolean containsAngleBracket(String token) {
        // token이 null이면 꺾쇠 문자가 없다고 본다.
        if (token == null) {
            return false;
        }

        // iOS에서 Data description 형태로 보낼 때 <...>가 섞일 수 있어서 확인한다.
        return token.contains("<") || token.contains(">");
    }
}