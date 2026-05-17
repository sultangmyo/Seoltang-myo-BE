package com.seoltangmyo.sugarcat.domain.user.entity;

import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_users_provider_provider_id",
                        columnNames = {"provider", "provider_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id")
    private Cat cat;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "insulin_notification_enabled", nullable = false)
    private boolean insulinNotiEnabled;

    @Column(name = "blood_sugar_notification_enabled", nullable = false)
    private boolean bloodSugarNotiEnabled;

    @Column(name = "meal_notification_enabled", nullable = false)
    private boolean mealNotiEnabled;

    @Column(name = "weekly_report_notification_enabled", nullable = false)
    private boolean weeklyReportNotificationEnabled;

    @Column(name = "onboarding_completed", nullable = false)
    private boolean onboardingCompleted;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 20)
    private ProviderType provider;

    @Column(name = "provider_id", nullable = false, length = 100)
    private String providerId;

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

    public static User createSocialUser(ProviderType provider, String providerId) {
        User user = new User();

        user.provider = provider;
        user.providerId = providerId;

        user.insulinNotiEnabled = false;
        user.bloodSugarNotiEnabled = false;
        user.mealNotiEnabled = false;
        user.weeklyReportNotificationEnabled = false;

        user.onboardingCompleted = false;

        return user;
    }
  
    public void completeOnboarding() {
        this.onboardingCompleted = true;
    }

}