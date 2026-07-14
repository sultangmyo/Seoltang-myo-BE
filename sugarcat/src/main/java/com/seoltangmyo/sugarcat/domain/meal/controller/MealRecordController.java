package com.seoltangmyo.sugarcat.domain.meal.controller;

import com.seoltangmyo.sugarcat.domain.meal.dto.MealRecordCreateRequest;
import com.seoltangmyo.sugarcat.domain.meal.dto.MealRecordListResponse;
import com.seoltangmyo.sugarcat.domain.meal.dto.MealRecordUpdateRequest;
import com.seoltangmyo.sugarcat.domain.meal.service.MealRecordService;
import com.seoltangmyo.sugarcat.domain.user.dto.MessageResponse;
import com.seoltangmyo.sugarcat.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/meals")
@RequiredArgsConstructor
public class MealRecordController {

    private final MealRecordService mealRecordService;

    // 날짜별 식사 기록 조회
    // GET /api/v1/meals/me?date={date}
    @GetMapping("/me")
    public ResponseEntity<MealRecordListResponse> getMealRecords(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        UUID userId = userDetails.getUserId();

        // 요청으로 들어온 값 확인
        log.info("[MealRecordController] 식사 기록 조회 요청 - userId={}, date={}",
                userId, date);

        // 서비스에서 식사 기록 조회
        MealRecordListResponse response = mealRecordService.getMealRecords(userId, date);

        // 응답으로 나가는 값 확인
        log.info("[MealRecordController] 식사 기록 조회 응답 - userId={}, date={}, response={}",
                userId, date, response);

        // 200 OK 응답 반환
        return ResponseEntity.ok(response);
    }

    // 식사 기록 저장
    // POST /api/v1/meals/me
    @PostMapping("/me")
    public ResponseEntity<MessageResponse> createMealRecord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MealRecordCreateRequest request
    ) {
        UUID userId = userDetails.getUserId();

        // 요청으로 들어온 값 확인
        log.info("[MealRecordController] 식사 기록 저장 요청 - userId={}, request={}",
                userId, request);

        // 서비스에서 식사 기록 저장
        MessageResponse response = mealRecordService.createMealRecord(userId, request);

        // 응답으로 나가는 값 확인
        log.info("[MealRecordController] 식사 기록 저장 응답 - userId={}, response={}",
                userId, response);

        // 201 Created 응답 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 식사 기록 수정
    // PATCH /api/v1/meals/me
    @PatchMapping("/me")
    public ResponseEntity<MessageResponse> updateMealRecord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MealRecordUpdateRequest request
    ) {
        UUID userId = userDetails.getUserId();

        // 요청으로 들어온 값 확인
        log.info("[MealRecordController] 식사 기록 수정 요청 - userId={}, request={}",
                userId, request);

        // 서비스에서 식사 기록 수정
        MessageResponse response = mealRecordService.updateMealRecord(userId, request);

        // 응답으로 나가는 값 확인
        log.info("[MealRecordController] 식사 기록 수정 응답 - userId={}, response={}",
                userId, response);

        // 200 OK 응답 반환
        return ResponseEntity.ok(response);
    }
}
