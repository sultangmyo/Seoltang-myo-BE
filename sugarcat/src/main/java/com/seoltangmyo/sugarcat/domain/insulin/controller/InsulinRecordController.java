package com.seoltangmyo.sugarcat.domain.insulin.controller;

import com.seoltangmyo.sugarcat.domain.insulin.dto.InsulinRecordCreateRequest;
import com.seoltangmyo.sugarcat.domain.insulin.service.InsulinRecordService;
import com.seoltangmyo.sugarcat.domain.user.dto.MessageResponse;
import com.seoltangmyo.sugarcat.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/insulin-records")
@RequiredArgsConstructor
public class InsulinRecordController {

    private final InsulinRecordService insulinRecordService;

    // 인슐린 투여 기록 저장
    // POST /api/v1/insulin-records/me
    @PostMapping("/me")
    public ResponseEntity<MessageResponse> createInsulinRecord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody InsulinRecordCreateRequest request
    ) {
        UUID userId = userDetails.getUserId();
        MessageResponse response = insulinRecordService.createInsulinRecord(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
