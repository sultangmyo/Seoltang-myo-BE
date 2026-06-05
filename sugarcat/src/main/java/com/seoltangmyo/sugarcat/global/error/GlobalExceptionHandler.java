package com.seoltangmyo.sugarcat.global.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;

// 공통 에러 응답 포맷: {"status": xxx, "message": "..."}
// 프론트(iOS)에서 status + message 두 필드로 모든 에러를 일관되게 처리할 수 있도록 통일
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비즈니스 로직 오류 (사용자 없음, 기록 없음, 중복 기록, 잘못된 값 등)
    // → 400 Bad Request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    // ResponseStatusException (초대코드 401 등 명세에서 상태 코드를 직접 지정하는 경우)
    // → 예외가 담고 있는 status 코드 그대로 반환
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException e) {
        return ResponseEntity
                .status(e.getStatusCode())
                .body(new ErrorResponse(e.getStatusCode().value(), e.getReason()));
    }

    // Request body 누락 / JSON 파싱 오류 (필드 타입 불일치, 빈 body 등)
    // → 400 Bad Request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("요청 본문 파싱 오류: {}", e.getMessage());
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "요청 본문을 읽을 수 없습니다. 필드 타입과 형식을 확인해주세요."));
    }

    // 외부 API 타임아웃 / 연결 오류 (Apple JWKS, 카카오 API 3초 초과 등)
    // → 503 Service Unavailable
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleResourceAccess(ResourceAccessException e) {
        log.error("외부 API 연결 오류: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "외부 서비스와의 통신에 실패했습니다. 잠시 후 다시 시도해주세요."));
    }

    // 서버 내부 오류 (카카오 서버 5xx 오류 등 IllegalStateException)
    // → 500 Internal Server Error
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException e) {
        log.error("서버 내부 오류 (IllegalStateException): {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류가 발생했습니다."));
    }

    // 예상치 못한 모든 예외 (최후 방어선)
    // → 500 Internal Server Error
    // 클라이언트에게는 일반 메시지만, 서버 로그에는 스택트레이스 기록
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("예상치 못한 서버 오류", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 오류가 발생했습니다. 관리자에게 문의해주세요."));
    }
}
