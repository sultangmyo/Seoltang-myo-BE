package com.seoltangmyo.sugarcat.domain.auth.dto;

import java.util.UUID;

// /refresh 응답
public record TokenRefreshResponse (
        String accessToken,
        UUID userId
){
}
