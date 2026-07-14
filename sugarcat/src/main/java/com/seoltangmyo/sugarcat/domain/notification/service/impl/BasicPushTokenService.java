package com.seoltangmyo.sugarcat.domain.notification.service.impl;

import com.seoltangmyo.sugarcat.domain.notification.dto.DeviceTokenRequest;
import com.seoltangmyo.sugarcat.domain.notification.service.PushTokenService;
import com.seoltangmyo.sugarcat.domain.notification.type.PlatformType;
import com.seoltangmyo.sugarcat.domain.user.entity.User;
import com.seoltangmyo.sugarcat.domain.user.repository.UserRepository;
import com.seoltangmyo.sugarcat.global.error.BusinessException;
import com.seoltangmyo.sugarcat.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicPushTokenService implements PushTokenService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void registerDeviceToken(UUID userId, DeviceTokenRequest request) {

        if (request.platform() != PlatformType.IOS) {
            throw new IllegalArgumentException("지원하지 않는 플랫폼입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.updateApnsDeviceToken(request.deviceToken());

        log.info("[APNs 토큰 등록 완료] userId={}", userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deactivateApnsToken(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.deactivateApnsToken();
    }

}
