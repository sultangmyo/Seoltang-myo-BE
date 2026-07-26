package com.seoltangmyo.sugarcat.domain.auth.client;

import com.seoltangmyo.sugarcat.domain.auth.dto.ApplePublicKeyResponse;
import com.seoltangmyo.sugarcat.global.error.BusinessException;
import com.seoltangmyo.sugarcat.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleApiClient {

    private static final String APPLE_PUBLIC_KEYS_URL = "https://appleid.apple.com/auth/keys";

    private final RestClient restClient;

    public ApplePublicKeyResponse getPublicKeys() {
        try {
            return restClient.get()
                    .uri(APPLE_PUBLIC_KEYS_URL)
                    .retrieve()
                    .body(ApplePublicKeyResponse.class);
        } catch (RestClientException e) {
            log.error("Apple 공개키 조회 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_LOGIN_SERVICE_UNAVAILABLE);
        }
    }
}
