package com.seoltangmyo.sugarcat.domain.bloodsugar.controller;

import com.seoltangmyo.sugarcat.domain.bloodsugar.dto.BloodSugarRecordCreateRequest;
import com.seoltangmyo.sugarcat.domain.bloodsugar.dto.BloodSugarRecordCreateResponse;
import com.seoltangmyo.sugarcat.domain.bloodsugar.dto.BloodSugarRecordListResponse;
import com.seoltangmyo.sugarcat.domain.bloodsugar.service.BloodSugarRecordService;
import com.seoltangmyo.sugarcat.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/blood-sugar-records")
@RequiredArgsConstructor
public class BloodSugarRecordController {

    private final BloodSugarRecordService bloodSugarRecordService;

    // 혈당 기록 저장
    @PostMapping("/me")
    public ResponseEntity<BloodSugarRecordCreateResponse> createBloodSugarRecord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody BloodSugarRecordCreateRequest request
    ) {
        UUID userId = userDetails.getUserId();

        BloodSugarRecordCreateResponse response = bloodSugarRecordService.createBloodSugarRecord(userId, request);

        return ResponseEntity.ok(response);
    }

    // 날짜별 혈당 기록 조회
    @GetMapping("/me")
    public ResponseEntity<BloodSugarRecordListResponse> getBloodSugarRecordsByDate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        UUID userId = userDetails.getUserId();
        BloodSugarRecordListResponse response = bloodSugarRecordService.getBloodSugarRecordsByDate(userId, date);

        return ResponseEntity.ok(response);
    }

    // 혈당 기록 수정
//    @PatchMapping("/me")
//    public ResponseEntity<BloodSugarRecordResponse> updateBloodSugarRecord(
//            @AuthenticationPrincipal CustomUserDetails userDetails,
//            @RequestBody BloodSugarRecordUpdateRequest request
//    ) {
//        UUID userId = userDetails.getUserId();
//        BloodSugarRecordResponse response = bloodSugarRecordService.updateBloodSugarRecord(userId, request);
//
//        return ResponseEntity.ok(response);
//    }

    // 혈당 기록 삭제
//    @DeleteMapping("/me")
//    public ResponseEntity<Void> deleteBloodSugarRecord(
//            @AuthenticationPrincipal CustomUserDetails userDetails,
//            @RequestParam("recordId") Long recordId
//    ) {
//        UUID userId = userDetails.getUserId();
//        bloodSugarRecordService.deleteBloodSugarRecord(userId, recordId);
//
//        return ResponseEntity.noContent().build();
//    }

}
