package com.seoltangmyo.sugarcat.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoUserInfoResponse(
        Long id
        // 카카오에서 내려주는 회원 고유 id
) {
}