package com.seoltangmyo.sugarcat.domain.cat.controller;

import com.seoltangmyo.sugarcat.domain.cat.dto.CatCreateRequest;
import com.seoltangmyo.sugarcat.domain.cat.dto.CatInfoResponse;
import com.seoltangmyo.sugarcat.domain.cat.dto.CatInfoUpdateRequest;
import com.seoltangmyo.sugarcat.domain.cat.dto.InviteCodeResponse;
import com.seoltangmyo.sugarcat.domain.cat.dto.InviteCodeValidateResponse;
import com.seoltangmyo.sugarcat.domain.cat.service.CatService;
import com.seoltangmyo.sugarcat.domain.user.dto.MessageResponse;
import com.seoltangmyo.sugarcat.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/cats")
@RequiredArgsConstructor
public class CatController {

    private final CatService catService;

    // 고양이 기본 정보 조회
    // GET /api/v1/cats/me
    // [추가: meaningGitt] 홈뷰 헤더용 고양이 이름 · 진단일 반환
    @GetMapping("/me")
    public ResponseEntity<CatInfoResponse> getCatInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        return ResponseEntity.ok(catService.getCatInfo(userId));
    }

    // 고양이 기본 정보 수정
    // PATCH /api/v1/cats/me
    // [추가: meaningGitt] 마이페이지에서 이름 · 생년월일 · 진단일 수정
    @PatchMapping("/me")
    public ResponseEntity<MessageResponse> updateCatInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CatInfoUpdateRequest request
    ) {
        UUID userId = userDetails.getUserId();
        return ResponseEntity.ok(catService.updateCatInfo(userId, request));
    }

    // 초대코드 조회
    // GET /api/v1/cats/me/invite-code
    @GetMapping("/me/invite-code")
    public ResponseEntity<InviteCodeResponse> getInviteCode(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        InviteCodeResponse response = catService.getInviteCode(userId);
        return ResponseEntity.ok(response);
    }

    // 초대코드 생성 (재생성)
    // PATCH /api/v1/cats/me/invite-code
    @PatchMapping("/me/invite-code")
    public ResponseEntity<InviteCodeResponse> generateInviteCode(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        InviteCodeResponse response = catService.generateInviteCode(userId);
        return ResponseEntity.ok(response);
    }

    // 초대코드 유효성 검증 + 공동 집사 합류
    // GET /api/v1/cats/invite?code={inviteCode}
    // 유효하면 user.catId 저장 후 고양이 정보 반환, 유효하지 않으면 401
    @GetMapping("/invite")
    public ResponseEntity<InviteCodeValidateResponse> validateInviteCode(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("code") String inviteCode
    ) {
        UUID userId = userDetails.getUserId();
        InviteCodeValidateResponse response = catService.validateInviteCode(userId, inviteCode);
        return ResponseEntity.ok(response);
    }

    // 고양이 신규 등록
    // POST /api/v1/cats
    // 온보딩 3-A 단계: 신규 고양이 정보 + 루틴 스케줄 한 번에 저장
    @PostMapping
    public ResponseEntity<MessageResponse> createCat(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CatCreateRequest request
    ) {
        log.info("##log## 고양이등록 컨트롤러 - request = {}", request);
        log.info("##log## 고양이등록 컨트롤러 - cat null 여부 = {}", request.cat() == null);

        UUID userId = userDetails.getUserId();
        MessageResponse response = catService.createCat(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
