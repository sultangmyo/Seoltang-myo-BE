package com.seoltangmyo.sugarcat.domain.notice.dto;

public record NoticeResponse(
        boolean enabled,
        String noticeId,
        String title,
        String message
) {
}
