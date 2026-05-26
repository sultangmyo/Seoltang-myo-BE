package com.seoltangmyo.sugarcat.domain.cat.entity;

import com.seoltangmyo.sugarcat.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "cats")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Cat extends BaseEntity {

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "diagnosed_date", nullable = false)
    private LocalDate diagnosedDate;

    @Column(name = "invite_code", length = 50)
    private String inviteCode;

    @Column(name = "meal_count", nullable = false)
    private int mealCount;

    @Column(name = "blood_sugar_count", nullable = false)
    private int bloodSugarCount;

    @Column(name = "insulin_count", nullable = false)
    private int insulinCount;

    public void assignInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }
}