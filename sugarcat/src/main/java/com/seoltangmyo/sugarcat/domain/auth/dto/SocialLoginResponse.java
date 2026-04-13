package com.seoltangmyo.sugarcat.domain.auth.dto;

// /apple /kakao 공통 응답
public record SocialLoginResponse (
        String accessToken,
        String refreshToken,
        boolean isNewUser // api 명세에 없으나 필요하다고 판단
){
}
