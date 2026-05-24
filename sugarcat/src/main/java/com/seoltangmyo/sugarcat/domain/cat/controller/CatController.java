package com.seoltangmyo.sugarcat.domain.cat.controller;

import com.seoltangmyo.sugarcat.domain.cat.dto.CatCreateRequest;
import com.seoltangmyo.sugarcat.domain.cat.service.CatService;
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
@RequestMapping("/api/v1/cats")
@RequiredArgsConstructor
public class CatController {

    private final CatService catService;

    // 고양이 신규 등록
    // POST /api/v1/cats
    // 온보딩 3-A 단계: 신규 고양이 정보 + 루틴 스케줄 한 번에 저장
    @PostMapping
    public ResponseEntity<MessageResponse> createCat(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CatCreateRequest request
    ) {
        UUID userId = userDetails.getUserId();
        MessageResponse response = catService.createCat(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
