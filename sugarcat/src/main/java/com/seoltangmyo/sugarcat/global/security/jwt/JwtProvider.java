package com.seoltangmyo.sugarcat.global.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {
    private final SecretKey secretKey;
    private final JwtProperties jwtProperties;

    public JwtProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        String secret = jwtProperties.getSecret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("jwt.secret must not be blank");
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) { // HS256 minimum: 32 bytes
            throw new IllegalStateException("jwt.secret must be at least 32 bytes for HS256");
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(UUID userId) {
        return createToken(userId, "ACCESS", jwtProperties.getAccessTokenExpiration());
    }

    public String createRefreshToken(UUID userId) {
        return createToken(userId, "REFRESH", jwtProperties.getRefreshTokenExpiration());
    }

    private String createToken(UUID userId, String type, long expireTime) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expireTime);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("type", type)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    public UUID getUserId(String token) {
        return UUID.fromString(parse(token).getSubject());
    }

    public String getType(String token) {
        return parse(token).get("type", String.class);
    }

    public boolean validate(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public LocalDateTime getExpiration(String token) {
        Date exp = parse(token).getExpiration();
        return exp.toInstant()
                .atOffset(ZoneOffset.UTC)
                .toLocalDateTime();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public UUID getUserId(Claims claims) {
        return UUID.fromString(claims.getSubject());
    }

    public String getType(Claims claims) {
        return claims.get("type", String.class);
    }
}