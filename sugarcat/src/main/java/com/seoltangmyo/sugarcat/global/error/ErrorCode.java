package com.seoltangmyo.sugarcat.global.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    INVALID_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "입력값을 확인해주세요."),
    INVALID_INVITE_CODE_FORMAT(HttpStatus.BAD_REQUEST, "INVALID_INVITE_CODE_FORMAT", "초대코드 형식이 올바르지 않습니다."),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증이 필요합니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", "유효하지 않은 Refresh Token입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "접근 권한이 없습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    CAT_NOT_FOUND(HttpStatus.NOT_FOUND, "CAT_NOT_FOUND", "등록된 고양이가 없습니다."),
    RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "RECORD_NOT_FOUND", "기록을 찾을 수 없습니다."),
    INVITE_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "INVITE_CODE_NOT_FOUND", "초대코드가 존재하지 않습니다."),

    DUPLICATE_RECORD(HttpStatus.CONFLICT, "DUPLICATE_RECORD", "이미 해당 회차의 기록이 존재합니다."),

    EXTERNAL_LOGIN_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "EXTERNAL_LOGIN_SERVICE_UNAVAILABLE", "외부 로그인 서비스와 통신에 실패했습니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public int getStatusValue() {
        return status.value();
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
