package com.seoltangmyo.sugarcat.domain.auth.service;

import com.seoltangmyo.sugarcat.domain.auth.client.AppleApiClient;
import com.seoltangmyo.sugarcat.domain.auth.dto.ApplePublicKey;
import com.seoltangmyo.sugarcat.domain.auth.dto.ApplePublicKeyResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class AppleTokenVerifier {

    private static final String APPLE_ISSUER = "https://appleid.apple.com";

    private final AppleApiClient appleApiClient;

    @Value("${oauth2.apple.client-id}")
    private String appleClientId;

    public String verifyAndExtractSubject(String identityToken) {
        JwsHeader header = getHeader(identityToken);

        ApplePublicKey applePublicKey = findMatchingKey(
                header.getKeyId(),
                header.getAlgorithm()
        );

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

    private JwsHeader getHeader(String identityToken) {
        return Jwts.parser()
                .build()
                .parseSignedClaims(identityToken)
                .getHeader();
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