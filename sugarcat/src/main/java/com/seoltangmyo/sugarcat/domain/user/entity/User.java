package com.seoltangmyo.sugarcat.domain.user.entity;

import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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







}
