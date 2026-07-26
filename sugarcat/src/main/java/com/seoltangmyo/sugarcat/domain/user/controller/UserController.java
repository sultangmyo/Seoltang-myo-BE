package com.seoltangmyo.sugarcat.domain.user.controller;

import com.seoltangmyo.sugarcat.domain.user.dto.MessageResponse;
import com.seoltangmyo.sugarcat.domain.user.dto.NicknameUpdateRequest;
import com.seoltangmyo.sugarcat.domain.user.dto.NotificationInfoResponse;
import com.seoltangmyo.sugarcat.domain.user.dto.NotificationSingleUpdateRequest;
import com.seoltangmyo.sugarcat.domain.user.dto.NotificationUpdateRequest;
import com.seoltangmyo.sugarcat.domain.user.dto.UserMeResponse;
import com.seoltangmyo.sugarcat.domain.user.service.UserService;
import com.seoltangmyo.sugarcat.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "User", description = "사용자 API")
@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 내 정보 조회 (닉네임 + 가족 집사 목록)
    // GET /api/v1/users/me
    @Operation(summary = "내 정보 조회", description = "닉네임 + 같은 고양이를 가진 집사 목록")
    @GetMapping
    public ResponseEntity<UserMeResponse> getUserMe(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        UserMeResponse response = userService.getUserMe(userId);
        return ResponseEntity.ok(response);
    }

    // 닉네임 수정
    // PATCH /api/v1/users/me/nickname
    // 온보딩 2단계 및 마이페이지 공용
    @Operation(summary = "닉네임 수정")
    @PatchMapping("/nickname")
    public ResponseEntity<MessageResponse> updateNickname(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody NicknameUpdateRequest request
    ) {
        UUID userId = userDetails.getUserId();
        MessageResponse response = userService.updateNickname(userId, request);
        return ResponseEntity.ok(response);
    }

    // 알림 전체 수정
    // PATCH /api/v1/users/me/notification
    // 온보딩 4단계: 알림 허용 시 true, 거부 시 false 전달 (type 파라미터 없을 때)
    @Operation(summary = "알림 전체 수정", description = "type 파라미터 없이 호출 시 4가지 알림 전체 ON/OFF")
    @PatchMapping(value = "/notification", params = "!type")
    public ResponseEntity<MessageResponse> updateNotification(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody NotificationUpdateRequest request
    ) {
        UUID userId = userDetails.getUserId();
        MessageResponse response = userService.updateNotification(userId, request);
        return ResponseEntity.ok(response);
    }

    // 알림 정보 조회
    // GET /api/v1/users/me/notification
    @Operation(summary = "알림 정보 조회")
    @GetMapping("/notification")
    public ResponseEntity<NotificationInfoResponse> getNotificationInfo(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        return ResponseEntity.ok(userService.getNotificationInfo(userId));
    }

    // 알림 개별 수정
    // PATCH /api/v1/users/me/notification?type={type}
    // type: insulin / blood / meal / weekly
    @Operation(summary = "알림 개별 수정", description = "type: insulin / blood / meal / weekly")
    @PatchMapping(value = "/notification", params = "type")
    public ResponseEntity<MessageResponse> updateSingleNotification(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("type") String type,
            @Valid @RequestBody NotificationSingleUpdateRequest request
    ) {
        UUID userId = userDetails.getUserId();
        return ResponseEntity.ok(userService.updateSingleNotification(userId, type, request));
    }

    // 사용자 삭제
    // DELETE /api/v1/users/me
    @Operation(summary = "사용자 탈퇴")
    @DeleteMapping
    public ResponseEntity<MessageResponse> deleteUser(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        return ResponseEntity.ok(userService.deleteUser(userId));
    }

}
