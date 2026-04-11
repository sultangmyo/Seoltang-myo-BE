package com.seoltangmyo.sugarcat.domain.caresetting.entity;

import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table
        (
                name = "cat_care_settings",
                uniqueConstraints = @UniqueConstraint(columnNames = "cat_id")
        )
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CatCareSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID catCareSettingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id", nullable = false)
    private Cat cat;

    @Column(name = "meal_count", nullable = false)
    private int mealCount;

    @Column(name = "blood_sugar_count", nullable = false)
    private int bloodSugarCount;

    @Column(name = "insulin_count", nullable = false)
    private int insulinCount;


}
