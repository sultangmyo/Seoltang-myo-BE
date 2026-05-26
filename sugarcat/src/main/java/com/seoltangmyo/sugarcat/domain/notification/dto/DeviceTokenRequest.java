package com.seoltangmyo.sugarcat.domain.notification.dto;

import com.seoltangmyo.sugarcat.domain.notification.type.PlatformType;

public record DeviceTokenRequest(
        String deviceToken,
        PlatformType platform
) {
}
