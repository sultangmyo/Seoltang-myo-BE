package com.seoltangmyo.sugarcat.domain.bloodsugar.controller;

import com.seoltangmyo.sugarcat.domain.bloodsugar.dto.BloodSugarRecordCreateRequest;
import com.seoltangmyo.sugarcat.domain.bloodsugar.dto.BloodSugarRecordCreateResponse;
import com.seoltangmyo.sugarcat.domain.bloodsugar.dto.BloodSugarRecordListResponse;
import com.seoltangmyo.sugarcat.domain.bloodsugar.dto.BloodSugarRecordUpdateRequest;
import com.seoltangmyo.sugarcat.domain.bloodsugar.service.BloodSugarRecordService;
import com.seoltangmyo.sugarcat.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@Tag(name = "BloodSugar", description = "혈당 기록 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/blood-sugar-records")
@RequiredArgsConstructor
public class BloodSugarRecordController {

    private final BloodSugarRecordService bloodSugarRecordService;

    // 혈당 기록 저장
    @Operation(summary = "혈당 기록 저장")
    @PostMapping("/me")
    public ResponseEntity<BloodSugarRecordCreateResponse> createBloodSugarRecord(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody BloodSugarRecordCreateRequest request
    ) {
        UUID userId = userDetails.getUserId();

        // 요청으로 들어온 값 확인
        log.info("[BloodSugarRecordController] 혈당 기록 저장 요청 - userId={}, request={}",
                userId, request);

        BloodSugarRecordCreateResponse response = bloodSugarRecordService.createBloodSugarRecord(userId, request);

        // 응답으로 나가는 값 확인
        log.info("[BloodSugarRecordController] 혈당 기록 저장 응답 - userId={}, response={}",
                userId, response);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 날짜별 혈당 기록 조회
    @Operation(summary = "날짜별 혈당 기록 조회")
    @GetMapping("/me")
    public ResponseEntity<BloodSugarRecordListResponse> getBloodSugarRecordsByDate(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        UUID userId = userDetails.getUserId();

        // 요청으로 들어온 값 확인
        log.info("[BloodSugarRecordController] 혈당 기록 조회 요청 - userId={}, date={}",
                userId, date);

        // 서비스에서 날짜별 혈당 기록 조회
        BloodSugarRecordListResponse response =
                bloodSugarRecordService.getBloodSugarRecordsByDate(userId, date);

        // 응답으로 나가는 값 확인
        log.info("[BloodSugarRecordController] 혈당 기록 조회 응답 - userId={}, date={}, response={}",
                userId, date, response);

        // 200 OK 응답 반환
        return ResponseEntity.ok(response);
    }

    // 혈당 기록 수정
    @Operation(summary = "혈당 기록 수정")
    @PatchMapping("/me")
    public ResponseEntity<Void> updateBloodSugarRecord(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody BloodSugarRecordUpdateRequest request
    ) {
        UUID userId = userDetails.getUserId();

        // 요청으로 들어온 값 확인
        log.info("[BloodSugarRecordController] 혈당 기록 수정 요청 - userId={}, request={}",
                userId, request);

        // 서비스에서 혈당 기록 수정
        bloodSugarRecordService.updateBloodSugarRecord(userId, request);

        // 응답 body는 없지만, 수정 성공 여부를 로그로 확인
        log.info("[BloodSugarRecordController] 혈당 기록 수정 성공 - userId={}, request={}",
                userId, request);

        // 204 No Content 응답 반환
        return ResponseEntity.noContent().build();
    }

    // 혈당 기록 삭제
    @Operation(summary = "혈당 기록 삭제")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteBloodSugarRecord(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("sequence") int sequence
    ) {
        UUID userId = userDetails.getUserId();

        // 요청으로 들어온 값 확인
        log.info("[BloodSugarRecordController] 혈당 기록 삭제 요청 - userId={}, date={}, sequence={}",
                userId, date, sequence);

        // 서비스에서 혈당 기록 삭제
        bloodSugarRecordService.deleteBloodSugarRecord(userId, date, sequence);

        // 응답 body는 없지만, 삭제 성공 여부를 로그로 확인
        log.info("[BloodSugarRecordController] 혈당 기록 삭제 성공 - userId={}, date={}, sequence={}",
                userId, date, sequence);

        // 204 No Content 응답 반환
        return ResponseEntity.noContent().build();
    }

}
