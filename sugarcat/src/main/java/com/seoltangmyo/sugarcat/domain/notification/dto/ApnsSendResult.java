package com.seoltangmyo.sugarcat.domain.notification.dto;

public record ApnsSendResult(
        boolean success,
        boolean invalidToken,
        String reason
) {
    public static ApnsSendResult accepted() {
        return new ApnsSendResult(true, false, null);
    }

    public static ApnsSendResult rejected(String reason, boolean invalidToken) {
        return new ApnsSendResult(false, invalidToken, reason);
    }

    public static ApnsSendResult failed(String reason) {
        return new ApnsSendResult(false, false, reason);
    }
}
