package com.seoltangmyo.sugarcat.domain.meal.controller;

import com.seoltangmyo.sugarcat.domain.meal.dto.MealRecordCreateRequest;
import com.seoltangmyo.sugarcat.domain.meal.dto.MealRecordListResponse;
import com.seoltangmyo.sugarcat.domain.meal.dto.MealRecordUpdateRequest;
import com.seoltangmyo.sugarcat.domain.meal.service.MealRecordService;
import com.seoltangmyo.sugarcat.domain.user.dto.MessageResponse;
import com.seoltangmyo.sugarcat.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
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
        return ResponseEntity.ok(mealRecordService.getMealRecords(userId, date));
    }

    // 식사 기록 저장
    // POST /api/v1/meals/me?date={date}&sequence={sequence}
    @PostMapping("/me")
    public ResponseEntity<MessageResponse> createMealRecord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("sequence") int sequence,
            @RequestBody MealRecordCreateRequest request
    ) {
        UUID userId = userDetails.getUserId();
        MessageResponse response = mealRecordService.createMealRecord(userId, date, sequence, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 식사 기록 수정
    // PATCH /api/v1/meals/me
    @PatchMapping("/me")
    public ResponseEntity<MessageResponse> updateMealRecord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody MealRecordUpdateRequest request
    ) {
        UUID userId = userDetails.getUserId();
        return ResponseEntity.ok(mealRecordService.updateMealRecord(userId, request));
    }
}
