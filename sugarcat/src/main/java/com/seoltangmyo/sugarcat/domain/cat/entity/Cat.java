package com.seoltangmyo.sugarcat.domain.cat.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "cats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Cat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID catId;

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

}