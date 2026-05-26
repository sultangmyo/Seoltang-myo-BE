package com.seoltangmyo.sugarcat.domain.user.service;

import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.cat.repository.CatRepository;
import com.seoltangmyo.sugarcat.domain.user.dto.CatUpdateRequest;
import com.seoltangmyo.sugarcat.domain.user.dto.MessageResponse;
import com.seoltangmyo.sugarcat.domain.user.dto.NicknameUpdateRequest;
import com.seoltangmyo.sugarcat.domain.user.dto.NotificationUpdateRequest;
import com.seoltangmyo.sugarcat.domain.user.dto.UserMeResponse;
import com.seoltangmyo.sugarcat.domain.user.entity.User;
import com.seoltangmyo.sugarcat.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CatRepository catRepository;

    // 내 정보 조회 (닉네임 + 같은 고양이를 가진 다른 집사 목록)
    // GET /api/v1/users/me
    @Transactional
    public UserMeResponse getUserMe(UUID userId) {
        User me = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cat cat = me.getCat();

        List<UserMeResponse.FamilyMember> family = List.of();
        if (cat != null) {
            family = userRepository.findAllByCat(cat).stream()
                    .filter(u -> !u.getId().equals(userId))
                    .map(u -> new UserMeResponse.FamilyMember(u.getNickname()))
                    .toList();
        }

        return new UserMeResponse(me.getNickname(), family);
    }

    // 닉네임 수정
    // 온보딩 최초 설정 및 마이페이지 수정 모두 이 메서드를 공용으로 사용
    @Transactional
    public MessageResponse updateNickname(UUID userId, NicknameUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.updateNickname(request.nickname());

        return new MessageResponse("사용자 정보가 수정되었습니다.");
    }

    // 알림 전체 수정
    // 온보딩에서 알림 허용 시 → notificationEnabled = true → 4가지 알림 전부 ON
    // 온보딩에서 알림 거부 시 → notificationEnabled = false → 4가지 알림 전부 OFF
    // 마이페이지에서 개별 알림 수정이 생기면 별도 메서드 추가 필요
    @Transactional
    public MessageResponse updateNotification(UUID userId, NotificationUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.updateNotification(request.notificationEnabled());

        return new MessageResponse("사용자 알림이 수정되었습니다.");
    }

    // 사용자 고양이 수정 (공동 집사 합류)
    // 초대코드로 고양이 검증 + user.catId 저장을 한 번에 처리
    // UUID 노출 없이 inviteCode를 식별자로 사용
    // 유효하지 않은 초대코드면 예외 발생 → 프론트 에러 토스트 표시
    @Transactional
    public MessageResponse updateCat(UUID userId, CatUpdateRequest request) {
        // 초대코드로 고양이 조회 (유효하지 않으면 예외)
        Cat cat = catRepository.findByInviteCode(request.inviteCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "초대코드가 존재하지 않습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.assignCat(cat);

        return new MessageResponse("사용자 고양이가 수정되었습니다.");
    }
}
