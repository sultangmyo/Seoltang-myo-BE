package com.seoltangmyo.sugarcat.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record KakaoLoginRequest (
        @NotBlank(message = "카카오 Access Token은 필수입니다.")
        String accessToken // 카카오 SDK에서 받은 Access Token
){
}
