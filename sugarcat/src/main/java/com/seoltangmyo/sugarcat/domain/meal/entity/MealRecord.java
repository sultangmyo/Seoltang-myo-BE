package com.seoltangmyo.sugarcat.domain.meal.entity;

import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.user.entity.User;
import com.seoltangmyo.sugarcat.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "meal_records")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MealRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id", nullable = false)
    private Cat cat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by_user_id")
    private User recordedBy;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "record_time", nullable = false)
    private LocalTime recordTime;

    @Column(name = "sequence", nullable = false)
    private int sequence;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_status", length = 20, nullable = false)
    private MealStatus mealStatus;

    public static MealRecord create(Cat cat, User recordedBy, LocalDate recordDate, LocalTime recordTime, int sequence, MealStatus mealStatus) {
        MealRecord mealRecord = new MealRecord();
        mealRecord.cat = cat;
        mealRecord.recordedBy = recordedBy;
        mealRecord.recordDate = recordDate;
        mealRecord.recordTime = recordTime;
        mealRecord.sequence = sequence;
        mealRecord.mealStatus = mealStatus;
        return mealRecord;
    }

    // 식사 기록 수정 (PATCH /api/v1/meals/me)
    // date + sequence는 식별자로 사용하므로 수정 대상에서 제외
    public void update(LocalTime recordTime, MealStatus mealStatus) {
        this.recordTime = recordTime;
        this.mealStatus = mealStatus;
    }
}