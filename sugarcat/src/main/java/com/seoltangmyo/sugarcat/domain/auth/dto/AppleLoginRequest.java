package com.seoltangmyo.sugarcat.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record AppleLoginRequest (
        @NotBlank(message = "Apple identityToken은 필수입니다.")
        String identityToken // Apple SDK에서 받은 Identity Token
){
}
