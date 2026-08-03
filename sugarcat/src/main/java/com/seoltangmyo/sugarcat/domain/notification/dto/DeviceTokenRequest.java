package com.seoltangmyo.sugarcat.domain.notification.dto;

import com.seoltangmyo.sugarcat.domain.notification.type.PlatformType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeviceTokenRequest(
        @NotBlank(message = "디바이스 토큰은 필수입니다.")
        String deviceToken,
        @NotNull(message = "플랫폼은 필수입니다.")
        PlatformType platform
) {
}
