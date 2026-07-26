package com.seoltangmyo.sugarcat.domain.notification.controller;

import com.seoltangmyo.sugarcat.domain.notification.dto.DeviceTokenRequest;
import com.seoltangmyo.sugarcat.domain.notification.service.PushTokenService;
import com.seoltangmyo.sugarcat.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Notification", description = "알림 API")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final PushTokenService pushTokenService;

    @Operation(summary = "디바이스 토큰 등록")
    @PostMapping("/device-token")
    public ResponseEntity<Void> registerDeviceToken(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody DeviceTokenRequest request
    ){
        UUID userId = userDetails.getUserId();
        pushTokenService.registerDeviceToken(userId, request);
        return ResponseEntity.noContent().build();
    }
}
