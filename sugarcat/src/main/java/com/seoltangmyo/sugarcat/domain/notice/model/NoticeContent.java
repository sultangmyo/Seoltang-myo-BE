package com.seoltangmyo.sugarcat.domain.notice.model;

import com.seoltangmyo.sugarcat.domain.notice.dto.NoticeResponse;

public class NoticeContent {

    private static final boolean ENABLED = false;
    private static final String NOTICE_ID = "service-end-2026-12";
    private static final String TITLE = "서비스 종료 예정 안내";
    private static final String MESSAGE = "본 앱은 2026년 12월 30일부로 서비스 운영을 종료할 예정입니다. 종료 전까지 필요한 기록을 확인하거나 백업해 주세요.";

    private NoticeContent() {
    }

    public static NoticeResponse activeNotice() {
        if (!ENABLED) {
            return new NoticeResponse(false, null, null, null);
        }

        return new NoticeResponse(true, NOTICE_ID, TITLE, MESSAGE);
    }
}
