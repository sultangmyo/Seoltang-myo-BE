package com.seoltangmyo.sugarcat.domain.auth.service;

import com.seoltangmyo.sugarcat.domain.auth.client.AppleApiClient;
import com.seoltangmyo.sugarcat.domain.auth.dto.ApplePublicKey;
import com.seoltangmyo.sugarcat.domain.auth.dto.ApplePublicKeyResponse;
import com.seoltangmyo.sugarcat.global.error.BusinessException;
import com.seoltangmyo.sugarcat.global.error.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleTokenVerifier {

    private static final String APPLE_ISSUER = "https://appleid.apple.com";

    private final AppleApiClient appleApiClient;
    private final ObjectMapper objectMapper;

    @Value("${apple.login.client-id}")
    private String appleClientId;

    public String verifyAndExtractSubject(String identityToken) {
        log.info("##log## 애플검증 - 진입");

        if (identityToken == null || identityToken.isBlank()) {
            log.warn("##log## 애플검증 - identityToken 없음");
            throw new IllegalArgumentException("Apple identityToken은 필수입니다.");
        }

        log.info("##log## 애플검증 - 헤더 파싱 시작");
        Map<String, String> header = parseHeader(identityToken);
        log.info("##log## 애플검증 - 헤더 파싱 성공");

        String kid = header.get("kid");
        String alg = header.get("alg");

        log.info("애플 토큰 kid 존재 여부 = {}", kid != null);
        log.info("애플 토큰 alg = {}", alg);

        if (kid == null || alg == null) {
            log.warn("##log## 애플검증 - kid 또는 alg 없음");
            throw new IllegalArgumentException("Apple identityToken 헤더가 올바르지 않습니다.");
        }

        if (!"RS256".equals(alg)) {
            log.warn("##log## 애플검증 - 지원하지 않는 alg: {}", alg);
            throw new IllegalArgumentException("지원하지 않는 Apple identityToken 알고리즘입니다: " + alg);
        }

        log.info("##log## 애플검증 - Apple 공개키 조회 시작");
        ApplePublicKey applePublicKey = findMatchingKey(kid, alg);
        log.info("##log## 애플검증 - Apple 공개키 매칭 성공");

        log.info("##log## 애플검증 - PublicKey 생성 시작");
        PublicKey publicKey = createPublicKey(applePublicKey);
        log.info("##log## 애플검증 - PublicKey 생성 성공");

        log.info("##log## 애플검증 - JWT 서명/issuer/audience 검증 시작");
        Claims claims = Jwts.parser()
                .verifyWith(publicKey)
                .requireIssuer(APPLE_ISSUER)
                .requireAudience(appleClientId)
                .build()
                .parseSignedClaims(identityToken)
                .getPayload();
        log.info("##log## 애플검증 - JWT 검증 성공");

        String subject = claims.getSubject();

        if (subject == null || subject.isBlank()) {
            log.warn("##log## 애플검증 - subject 없음");
            throw new IllegalArgumentException("Apple identityToken에 subject가 없습니다.");
        }

        log.info("##log## 애플검증 - subject 추출 성공");
        log.info("애플 subject 길이 = {}", subject.length());

        return subject;
    }

    private Map<String, String> parseHeader(String identityToken) {
        try {
            String[] tokenParts = identityToken.split("\\."); // JWT를 header, payload, signature로 분리

            if (tokenParts.length != 3) {
                throw new IllegalArgumentException("identityToken 형식이 올바르지 않습니다.");
            }

            String headerBase64 = tokenParts[0];

            byte[] decodedHeader = Base64.getUrlDecoder().decode(headerBase64);

            String headerJson = new String(decodedHeader, StandardCharsets.UTF_8);

            return objectMapper.readValue(
                    headerJson,
                    new TypeReference<>() {
                    }
            );

        } catch (IllegalArgumentException e) {
            throw e;

        } catch (Exception e) {
            throw new IllegalArgumentException("identityToken 헤더 파싱에 실패했습니다.", e);
        }
    }

    private ApplePublicKey findMatchingKey(String kid, String alg) {
        ApplePublicKeyResponse response = appleApiClient.getPublicKeys();

        if (response == null || response.keys() == null) {
            throw new BusinessException(ErrorCode.EXTERNAL_LOGIN_SERVICE_UNAVAILABLE);
        }

        return response.keys().stream()
                .filter(key -> key.kid().equals(kid))
                .filter(key -> key.alg().equals(alg))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Apple identityToken과 일치하는 공개키가 없습니다."));
    }

    private PublicKey createPublicKey(ApplePublicKey applePublicKey) {
        try {
            byte[] nBytes = Base64.getUrlDecoder().decode(applePublicKey.n());
            byte[] eBytes = Base64.getUrlDecoder().decode(applePublicKey.e());

            BigInteger modulus = new BigInteger(1, nBytes);
            BigInteger exponent = new BigInteger(1, eBytes);

            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, exponent);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return keyFactory.generatePublic(publicKeySpec);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.EXTERNAL_LOGIN_SERVICE_UNAVAILABLE);
        }
    }
}
