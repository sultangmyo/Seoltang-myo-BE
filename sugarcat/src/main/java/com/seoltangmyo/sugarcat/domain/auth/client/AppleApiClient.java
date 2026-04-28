package com.seoltangmyo.sugarcat.domain.auth.client;

import com.seoltangmyo.sugarcat.domain.auth.dto.ApplePublicKeyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class AppleApiClient {

    private static final String APPLE_PUBLIC_KEYS_URL = "https://appleid.apple.com/auth/keys";

    private final RestClient restClient;

    public ApplePublicKeyResponse getPublicKeys() {
        return restClient.get()
                .uri(APPLE_PUBLIC_KEYS_URL)
                .retrieve()
                .body(ApplePublicKeyResponse.class);
    }
}
