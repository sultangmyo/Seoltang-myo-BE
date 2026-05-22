package com.seoltangmyo.sugarcat.domain.notification.controller;

import com.seoltangmyo.sugarcat.domain.notification.dto.DeviceTokenRequest;
import com.seoltangmyo.sugarcat.domain.notification.service.PushTokenService;
import com.seoltangmyo.sugarcat.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final PushTokenService pushTokenService;

    @PostMapping("/device-token")
    public ResponseEntity<Void> registerDeviceToken(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody DeviceTokenRequest request
    ){
        UUID userId = userDetails.getUserId();
        pushTokenService.registerDeviceToken(userId, request);
        return ResponseEntity.noContent().build();
    }
}
