package com.seoltangmyo.sugarcat.domain.auth.dto;

public record KakaoLoginRequest (
        String accessToken // 카카오 SDK에서 받은 Access Token
){
}
