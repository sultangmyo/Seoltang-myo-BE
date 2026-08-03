package com.seoltangmyo.sugarcat.domain.notification.service;

import com.seoltangmyo.sugarcat.domain.notification.dto.DeviceTokenRequest;

import java.util.UUID;

public interface PushTokenService {
    void registerDeviceToken(UUID userId, DeviceTokenRequest request);

    void deactivateApnsToken(UUID userId);
}
