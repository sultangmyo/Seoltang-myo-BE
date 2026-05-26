package com.seoltangmyo.sugarcat.domain.auth.service;

import com.seoltangmyo.sugarcat.domain.auth.client.AppleApiClient;
import com.seoltangmyo.sugarcat.domain.auth.dto.ApplePublicKey;
import com.seoltangmyo.sugarcat.domain.auth.dto.ApplePublicKeyResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
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

@Component
@RequiredArgsConstructor
public class AppleTokenVerifier {

    private static final String APPLE_ISSUER = "https://appleid.apple.com";

    private final AppleApiClient appleApiClient;
    private final ObjectMapper objectMapper;

    @Value("${apple.login.client-id}")
    private String appleClientId;

    public String verifyAndExtractSubject(String identityToken) {
        Map<String, String> header = parseHeader(identityToken);

        String kid = header.get("kid");
        String alg = header.get("alg");

        if (kid == null || alg == null) {
            throw new IllegalArgumentException("Apple identityToken 헤더가 올바르지 않습니다.");
        }

        ApplePublicKey applePublicKey = findMatchingKey(kid, alg);
        PublicKey publicKey = createPublicKey(applePublicKey);

        Claims claims = Jwts.parser()
                .verifyWith(publicKey)
                .requireIssuer(APPLE_ISSUER)
                .requireAudience(appleClientId)
                .build()
                .parseSignedClaims(identityToken)
                .getPayload();

        return claims.getSubject();
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
            throw new IllegalArgumentException("Apple 공개키를 가져오지 못했습니다.");
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
            throw new IllegalArgumentException("Apple 공개키 생성에 실패했습니다.", e);
        }
    }
}