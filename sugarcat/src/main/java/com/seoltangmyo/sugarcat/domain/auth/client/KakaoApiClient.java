package com.seoltangmyo.sugarcat.domain.auth.client;

import com.seoltangmyo.sugarcat.domain.auth.dto.KakaoUserInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;


@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoApiClient {

    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    private final RestClient restClient;

    public KakaoUserInfoResponse getUserInfo(String kakaoAccessToken) {
        try {
            return restClient.get()
                    .uri(KAKAO_USER_INFO_URL)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoAccessToken)
                    .retrieve()
                    .body(KakaoUserInfoResponse.class);
        } catch (HttpClientErrorException e) {
            throw new IllegalArgumentException("유효하지 않은 카카오 액세스 토큰입니다.");
        } catch (HttpServerErrorException e) {
            throw new IllegalStateException("카카오 서버 오류입니다. 잠시 후 다시 시도해주세요.");
        }
    }
}