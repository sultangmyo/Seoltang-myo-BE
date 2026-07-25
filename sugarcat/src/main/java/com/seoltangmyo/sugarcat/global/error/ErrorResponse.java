package com.seoltangmyo.sugarcat.global.error;

// 모든 에러 응답의 공통 포맷
// 프론트(iOS)는 status/code 기준으로 필요한 에러만 분기하고, message는 사용자 안내 문구로 사용
// 예시: {"status": 404, "code": "USER_NOT_FOUND", "message": "사용자를 찾을 수 없습니다."}
public record ErrorResponse(
        int status,
        String code,
        String message
) {

    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.getStatusValue(),
                errorCode.getCode(),
                errorCode.getMessage()
        );
    }

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(
                errorCode.getStatusValue(),
                errorCode.getCode(),
                message
        );
    }
}
