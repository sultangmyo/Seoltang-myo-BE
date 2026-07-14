package com.seoltangmyo.sugarcat.domain.auth.client;

import com.seoltangmyo.sugarcat.domain.auth.dto.KakaoUserInfoResponse;
import com.seoltangmyo.sugarcat.global.error.BusinessException;
import com.seoltangmyo.sugarcat.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;


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
            throw new BusinessException(ErrorCode.INVALID_INPUT, "유효하지 않은 카카오 액세스 토큰입니다.");
        } catch (HttpServerErrorException e) {
            log.error("카카오 서버 오류", e);
            throw new BusinessException(ErrorCode.EXTERNAL_LOGIN_SERVICE_UNAVAILABLE);
        } catch (RestClientException e) {
            log.error("카카오 API 호출 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_LOGIN_SERVICE_UNAVAILABLE);
        }
    }
}
