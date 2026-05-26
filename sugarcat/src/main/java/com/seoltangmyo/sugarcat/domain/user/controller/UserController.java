package com.seoltangmyo.sugarcat.domain.user.controller;

import com.seoltangmyo.sugarcat.domain.user.dto.CatUpdateRequest;
import com.seoltangmyo.sugarcat.domain.user.dto.MessageResponse;
import com.seoltangmyo.sugarcat.domain.user.dto.NicknameUpdateRequest;
import com.seoltangmyo.sugarcat.domain.user.dto.NotificationUpdateRequest;
import com.seoltangmyo.sugarcat.domain.user.service.UserService;
import com.seoltangmyo.sugarcat.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 닉네임 수정
    // PATCH /api/v1/users/me/nickname
    // 온보딩 2단계 및 마이페이지 공용
    @PatchMapping("/nickname")
    public ResponseEntity<MessageResponse> updateNickname(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody NicknameUpdateRequest request
    ) {
        UUID userId = userDetails.getUserId();
        MessageResponse response = userService.updateNickname(userId, request);
        return ResponseEntity.ok(response);
    }

    // 알림 전체 수정
    // PATCH /api/v1/users/me/notification
    // 온보딩 4단계: 알림 허용 시 true, 거부 시 false 전달
    @PatchMapping("/notification")
    public ResponseEntity<MessageResponse> updateNotification(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody NotificationUpdateRequest request
    ) {
        UUID userId = userDetails.getUserId();
        MessageResponse response = userService.updateNotification(userId, request);
        return ResponseEntity.ok(response);
    }

    // 사용자 고양이 수정 (공동 집사 합류)
    // PATCH /api/v1/users/me/cat
    // 온보딩 3-B 단계: 초대코드 검증 + user.catId 저장을 한 번에 처리
    // inviteCode가 유효하지 않으면 400 예외 → 프론트 에러 토스트
    @PatchMapping("/cat")
    public ResponseEntity<MessageResponse> updateCat(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CatUpdateRequest request
    ) {
        UUID userId = userDetails.getUserId();
        MessageResponse response = userService.updateCat(userId, request);
        return ResponseEntity.ok(response);
    }
}
