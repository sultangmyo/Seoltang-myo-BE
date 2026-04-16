package com.seoltangmyo.sugarcat.domain.auth.dto;

public record AppleLoginRequest (
        String identityToken // Apple SDK에서 받은 Identity Token
){
}