package com.seoltangmyo.sugarcat.domain.cat.dto;

// GET /api/v1/cats/me/invite-code, PATCH /api/v1/cats/me/invite-code 응답
public record InviteCodeResponse(
        String inviteCode
) {
}
