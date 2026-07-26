package com.seoltangmyo.sugarcat.domain.user.service;

import com.seoltangmyo.sugarcat.domain.cache.CatCacheEvictService;
import com.seoltangmyo.sugarcat.domain.cat.service.CatService;
import com.seoltangmyo.sugarcat.domain.user.dto.MessageResponse;
import com.seoltangmyo.sugarcat.domain.user.dto.NicknameUpdateRequest;
import com.seoltangmyo.sugarcat.domain.user.dto.NotificationInfoResponse;
import com.seoltangmyo.sugarcat.domain.user.dto.NotificationSingleUpdateRequest;
import com.seoltangmyo.sugarcat.domain.user.dto.NotificationUpdateRequest;
import com.seoltangmyo.sugarcat.domain.user.dto.UserMeResponse;
import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.user.entity.User;
import com.seoltangmyo.sugarcat.domain.user.repository.UserRepository;
import com.seoltangmyo.sugarcat.global.error.BusinessException;
import com.seoltangmyo.sugarcat.global.error.ErrorCode;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CatCacheEvictService catCacheEvictService;
    private final CatService catService;

    // 내 정보 조회 (닉네임 + 같은 고양이를 가진 다른 집사 목록)
    // GET /api/v1/users/me
    @Cacheable(
            cacheNames = "userMe",
            key = "#userId"
    )
    @Transactional
    public UserMeResponse getUserMe(UUID userId) {
        log.info("[내 정보 조회] userId={}", userId);

        User me = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Cat cat = me.getCat();

        List<UserMeResponse.FamilyMember> family = List.of();
        if (cat != null) {
            family = userRepository.findAllByCat(cat).stream()
                    .filter(u -> !u.getId().equals(userId))
                    .map(u -> new UserMeResponse.FamilyMember(u.getNickname()))
                    .toList();
        }

        log.info("[내 정보 조회 완료] userId={}, familyCount={}", userId, family.size());

        return new UserMeResponse(me.getNickname(), family);
    }

    // 닉네임 수정
    // 온보딩 최초 설정 및 마이페이지 수정 모두 이 메서드를 공용으로 사용
    @CacheEvict(
            cacheNames = "userMe",
            allEntries = true
    )
    @Transactional
    public MessageResponse updateNickname(UUID userId, NicknameUpdateRequest request) {
        log.info("[닉네임 수정] userId={}, nickname={}", userId, request.nickname());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.updateNickname(request.nickname());

        catCacheEvictService.evictAllRecordCaches();

        log.info("[닉네임 수정 완료] userId={}", userId);

        return new MessageResponse("사용자 정보가 수정되었습니다.");
    }

    // 알림 전체 수정
    // 온보딩에서 알림 허용 시 → notificationEnabled = true → 4가지 알림 전부 ON
    // 온보딩에서 알림 거부 시 → notificationEnabled = false → 4가지 알림 전부 OFF
    @CacheEvict(
            cacheNames = "userNotificationSettings",
            key = "#userId"
    )
    @Transactional
    public MessageResponse updateNotification(UUID userId, NotificationUpdateRequest request) {
        log.info("[알림 전체 수정] userId={}, enabled={}", userId, request.notificationEnabled());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.updateNotification(request.notificationEnabled());

        log.info("[알림 전체 수정 완료] userId={}", userId);

        return new MessageResponse("사용자 알림이 수정되었습니다.");
    }

    // 알림 정보 조회
    // GET /api/v1/users/me/notification
    @Cacheable(
            cacheNames = "userNotificationSettings",
            key = "#userId"
    )
    @Transactional(readOnly = true)
    public NotificationInfoResponse getNotificationInfo(UUID userId) {
        log.info("[알림 정보 조회] userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return new NotificationInfoResponse(
                user.isInsulinNotiEnabled(),
                user.isBloodSugarNotiEnabled(),
                user.isMealNotiEnabled(),
                user.isWeeklyReportNotificationEnabled()
        );
    }

    // 알림 개별 수정
    // PATCH /api/v1/users/me/notification?type={type}
    // type: insulin / blood / meal / weekly용
    @CacheEvict(
            cacheNames = "userNotificationSettings",
            key = "#userId"
    )
    @Transactional
    public MessageResponse updateSingleNotification(UUID userId, String type, NotificationSingleUpdateRequest request) {
        log.info("[알림 개별 수정] userId={}, type={}, enabled={}", userId, type, request.isEnabled());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // type별 응답 메시지
        String message = switch (type) {
            case "insulin" -> "인슐린 알람이 수정되었습니다.";
            case "blood"   -> "혈당 알림이 수정되었습니다.";
            case "meal"    -> "식이 알람이 수정되었습니다.";
            case "weekly"  -> "주간 리포트 알림이 수정되었습니다.";
            default -> throw new BusinessException(ErrorCode.INVALID_INPUT, "알 수 없는 알림 타입입니다: " + type);
        };

        user.updateSingleNotification(type, request.isEnabled());

        log.info("[알림 개별 수정 완료] userId={}, type={}", userId, type);

        return new MessageResponse(message);
    }

    // 사용자 삭제
    // DELETE /api/v1/users/me
    // DB 스키마: recorded_by_user_id → ON DELETE SET NULL (자동 처리)
    @Caching(evict = {
            @CacheEvict(
                    cacheNames = "userMe",
                    allEntries = true
            ),
            @CacheEvict(
                    cacheNames = "onboardingStatus",
                    key = "#userId"
            ),
            @CacheEvict(
                    cacheNames = "userNotificationSettings",
                    key = "#userId"
            )
    })
    @Transactional
    public MessageResponse deleteUser(UUID userId) {
        log.info("[사용자 삭제] userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Cat cat = user.getCat();

        boolean isLastUserOfCat = false;

        if (cat != null) {
            long catUserCount = userRepository.countByCat(cat);

            isLastUserOfCat = catUserCount == 1;

            log.info("[고양이 연결 사용자 수 확인] catId={}, userCount={}", cat.getId(), catUserCount);
        }

        userRepository.delete(user);

        if (isLastUserOfCat) {
            catService.deleteCat(cat);
        }

        catCacheEvictService.evictAllRecordCaches();

        log.info("[사용자 삭제 완료] userId={}", userId);

        return new MessageResponse("사용자 정보가 삭제되었습니다.");
    }

}
