package com.seoltangmyo.sugarcat.domain.schedule.controller;

import com.seoltangmyo.sugarcat.domain.schedule.dto.CareScheduleInfoResponse;
import com.seoltangmyo.sugarcat.domain.schedule.dto.CareScheduleUpdateRequest;
import com.seoltangmyo.sugarcat.domain.schedule.entity.CareScheduleType;
import com.seoltangmyo.sugarcat.domain.schedule.service.CareScheduleService;
import com.seoltangmyo.sugarcat.domain.user.dto.MessageResponse;
import com.seoltangmyo.sugarcat.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/care_schedules/me")
@RequiredArgsConstructor
public class CareScheduleController {

    private final CareScheduleService careScheduleService;

    // 식사 정보 조회
    // GET /api/v1/care_schedules/me/meal
    @GetMapping("/meal")
    public ResponseEntity<CareScheduleInfoResponse> getMealSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        return ResponseEntity.ok(careScheduleService.getSchedules(userId, CareScheduleType.MEAL));
    }

    // 혈당 체크 정보 조회
    // GET /api/v1/care_schedules/me/blood-sugar
    @GetMapping("/blood-sugar")
    public ResponseEntity<CareScheduleInfoResponse> getBloodSugarSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        return ResponseEntity.ok(careScheduleService.getSchedules(userId, CareScheduleType.BLOODSUGAR));
    }

    // 인슐린 정보 조회
    // GET /api/v1/care_schedules/me/insulin
    @GetMapping("/insulin")
    public ResponseEntity<CareScheduleInfoResponse> getInsulinSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();

        // 요청으로 들어온 값 확인
        log.info("[CareScheduleController] 인슐린 스케줄 조회 요청 - userId={}, scheduleType={}",
                userId, CareScheduleType.INSULIN);

        // 서비스에서 인슐린 스케줄 조회
        CareScheduleInfoResponse response = careScheduleService.getSchedules(userId, CareScheduleType.INSULIN);

        // 응답으로 나가는 값 확인
        log.info("[CareScheduleController] 인슐린 스케줄 조회 응답 - userId={}, scheduleType={}, response={}",
                userId, CareScheduleType.INSULIN, response);

        // 200 OK 응답 반환
        return ResponseEntity.ok(response);
    }

    // 식사 설정 수정
    // PATCH /api/v1/care_schedules/me/meal
    @PatchMapping("/meal")
    public ResponseEntity<MessageResponse> updateMealSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CareScheduleUpdateRequest request
    ) {
        UUID userId = userDetails.getUserId();
        return ResponseEntity.ok(careScheduleService.updateSchedules(userId, CareScheduleType.MEAL, request));
    }

    // 혈당 체크 설정 수정
    // PATCH /api/v1/care_schedules/me/blood-sugar
    @PatchMapping("/blood-sugar")
    public ResponseEntity<MessageResponse> updateBloodSugarSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CareScheduleUpdateRequest request
    ) {
        UUID userId = userDetails.getUserId();
        return ResponseEntity.ok(careScheduleService.updateSchedules(userId, CareScheduleType.BLOODSUGAR, request));
    }

    // 인슐린 설정 수정
    // PATCH /api/v1/care_schedules/me/insulin
    @PatchMapping("/insulin")
    public ResponseEntity<MessageResponse> updateInsulinSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CareScheduleUpdateRequest request
    ) {
        UUID userId = userDetails.getUserId();
        return ResponseEntity.ok(careScheduleService.updateSchedules(userId, CareScheduleType.INSULIN, request));
    }
}
