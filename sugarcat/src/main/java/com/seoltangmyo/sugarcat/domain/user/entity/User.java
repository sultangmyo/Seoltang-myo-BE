package com.seoltangmyo.sugarcat.domain.user.entity;

import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id", nullable = false)
    private Cat cat;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "insulin_notification_enabled", nullable = false)
    private boolean insulinNotiEnabled;

    @Column(name = "blood_sugar_notification_enabled", nullable = false)
    private boolean bloodSugarNotiEnabled;

    @Column(name = "meal_notification_enabled", nullable = false)
    private boolean mealNotiEnabled;


    // JWT + 소셜 로그인

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 20)
    private ProviderType provider; // APPLE, KAKAO

    @Column(name = "provider_id", nullable = false, length = 100)
    private String providerId; // 애플 sub, 카카오 user id

    @Column(name = "refresh_token", length = 500)
    private String refreshToken;

    @Column(name = "refresh_token_expires_at")
    private Instant refreshTokenExpiresAt;


    // 비즈니스 메서드

    public void updateRefreshToken(String refreshToken, Instant expiresAt) {
        this.refreshToken = refreshToken;
        this.refreshTokenExpiresAt = expiresAt;
    }

    public void clearRefreshToken() {
        this.refreshToken = null;
        this.refreshTokenExpiresAt = null;
    }
}
