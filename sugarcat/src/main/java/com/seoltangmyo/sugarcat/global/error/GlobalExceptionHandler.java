package com.seoltangmyo.sugarcat.global.error;

import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeParseException;

// 공통 에러 응답 포맷: {"status": xxx, "code": "...", "message": "..."}
// 프론트(iOS)는 status/code 기준으로 필요한 에러만 분기하고, 나머지는 공통 안내로 처리
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode, e.getMessage()));
    }

    // 기존 코드 호환용: 점진적으로 BusinessException으로 교체 예정
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(ErrorCode.INVALID_INPUT, e.getMessage()));
    }

    // 기존 코드 호환용: 가능하면 BusinessException으로 교체 예정
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException e) {
        String message = e.getReason() == null ? "요청을 처리할 수 없습니다." : e.getReason();
        return ResponseEntity
                .status(e.getStatusCode())
                .body(new ErrorResponse(e.getStatusCode().value(), "HTTP_STATUS_ERROR", message));
    }

    // Request body 누락 / JSON 파싱 오류 (필드 타입 불일치, 빈 body 등)
    // → 400 Bad Request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("요청 본문 파싱 오류: {}", e.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(ErrorCode.INVALID_INPUT, "요청 본문을 읽을 수 없습니다. 필드 타입과 형식을 확인해주세요."));
    }

    // @Valid 검증 실패
    // → 400 Bad Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst().filter(error -> error.getDefaultMessage() != null).map(DefaultMessageSourceResolvable::getDefaultMessage).orElse(ErrorCode.INVALID_INPUT.getMessage());

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(ErrorCode.INVALID_INPUT, message));
    }

    // 필수 query parameter 누락
    // → 400 Bad Request
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
        log.warn("필수 요청 파라미터 누락: {}", e.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(ErrorCode.INVALID_INPUT, "필수 요청 파라미터가 누락되었습니다: " + e.getParameterName()));
    }

    // query parameter 타입 변환 실패 (날짜 형식 오류, 숫자 형식 오류 등)
    // → 400 Bad Request
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("요청 파라미터 형식 오류: {}", e.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(ErrorCode.INVALID_INPUT, "요청 파라미터 형식이 올바르지 않습니다: " + e.getName()));
    }

    // 서비스 내부 날짜/시간 문자열 파싱 실패
    // → 400 Bad Request
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ErrorResponse> handleDateTimeParse(DateTimeParseException e) {
        log.warn("날짜/시간 파싱 오류: {}", e.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(ErrorCode.INVALID_INPUT, "날짜 또는 시간 형식이 올바르지 않습니다."));
    }

    // 외부 API 타임아웃 / 연결 오류 (Apple JWKS, 카카오 API 3초 초과 등)
    // → 503 Service Unavailable
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleResourceAccess(ResourceAccessException e) {
        log.error("외부 API 연결 오류: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponse.from(ErrorCode.EXTERNAL_LOGIN_SERVICE_UNAVAILABLE));
    }

    // DB/JPA 오류
    // → 500 Internal Server Error
    @ExceptionHandler({DataAccessException.class, PersistenceException.class})
    public ResponseEntity<ErrorResponse> handleDatabaseException(Exception e) {
        log.error("데이터베이스 처리 중 서버 오류", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    // 서버 내부 상태 오류
    // → 500 Internal Server Error
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException e) {
        log.error("서버 내부 상태 오류", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    // 예상치 못한 모든 예외 (최후 방어선)
    // → 500 Internal Server Error
    // 클라이언트에게는 일반 메시지만, 서버 로그에는 스택트레이스 기록
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("예상치 못한 서버 오류", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
