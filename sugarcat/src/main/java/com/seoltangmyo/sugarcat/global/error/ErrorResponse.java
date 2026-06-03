package com.seoltangmyo.sugarcat.global.error;

// 모든 에러 응답의 공통 포맷
// 프론트(iOS)가 status + message 두 필드만 파싱하면 모든 에러를 처리할 수 있도록 통일
// 예시: {"status": 400, "message": "사용자를 찾을 수 없습니다."}
public record ErrorResponse(
        int status,
        String message
) {
}
