package com.seoltangmyo.sugarcat.domain.insulin.controller;

import com.seoltangmyo.sugarcat.domain.insulin.dto.InsulinRecordCreateRequest;
import com.seoltangmyo.sugarcat.domain.insulin.dto.InsulinRecordListResponse;
import com.seoltangmyo.sugarcat.domain.insulin.service.InsulinRecordService;
import com.seoltangmyo.sugarcat.domain.user.dto.MessageResponse;
import com.seoltangmyo.sugarcat.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/insulin-records")
@RequiredArgsConstructor
public class InsulinRecordController {

    private final InsulinRecordService insulinRecordService;

    // 날짜별 인슐린 투여 기록 조회
    // GET /api/v1/insulin-records/me?date={date}
    @GetMapping("/me")
    public ResponseEntity<InsulinRecordListResponse> getInsulinRecords(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        // 로그인한 사용자의 id를 꺼냄
        UUID userId = userDetails.getUserId();

        // 요청으로 들어온 값 확인
        log.info("[InsulinRecordController] 인슐린 기록 조회 요청 - userId={}, date={}", userId, date);

        // 서비스에서 인슐린 기록 조회
        InsulinRecordListResponse response = insulinRecordService.getInsulinRecords(userId, date);

        // 응답으로 나가는 값 확인
        log.info("[InsulinRecordController] 인슐린 기록 조회 응답 - userId={}, date={}, response={}",
                userId, date, response);

        // 200 OK 응답 반환
        return ResponseEntity.ok(response);
    }

    // 인슐린 투여 기록 저장
    // POST /api/v1/insulin-records/me
    @PostMapping("/me")
    public ResponseEntity<MessageResponse> createInsulinRecord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody InsulinRecordCreateRequest request
    ) {
        // 로그인한 사용자의 id를 꺼냄
        UUID userId = userDetails.getUserId();

        // 요청으로 들어온 값 확인
        log.info("[InsulinRecordController] 인슐린 기록 저장 요청 - userId={}, request={}", userId, request);

        // 서비스에서 인슐린 기록 저장
        MessageResponse response = insulinRecordService.createInsulinRecord(userId, request);

        // 응답으로 나가는 값 확인
        log.info("[InsulinRecordController] 인슐린 기록 저장 응답 - userId={}, response={}", userId, response);

        // 201 Created 응답 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
