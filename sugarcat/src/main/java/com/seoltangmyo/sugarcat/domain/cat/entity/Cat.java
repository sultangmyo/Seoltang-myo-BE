package com.seoltangmyo.sugarcat.domain.cat.entity;

import com.seoltangmyo.sugarcat.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "cats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Cat extends BaseEntity {

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "diagnosed_date")
    private LocalDate diagnosedDate;

    @Column(name = "invite_code", length = 50)
    private String inviteCode;

    @Column(name = "meal_count", nullable = false)
    private int mealCount;

    @Column(name = "blood_sugar_count", nullable = false)
    private int bloodSugarCount;

    @Column(name = "insulin_count", nullable = false)
    private int insulinCount;

    // 신규 고양이 등록 시 사용하는 정적 팩토리 메서드
    // inviteCode는 생성 시점에 null → 추후 초대코드 발급 기능에서 채워질 예정
    public static Cat create(
            String name,
            LocalDate birthDate,       // null 허용 (프론트에서 "모르겠어요" 선택 시 null 전달)
            LocalDate diagnosedDate,
            int mealCount,
            int bloodSugarCount,
            int insulinCount
    ) {
        Cat cat = new Cat();
        cat.name = name;
        cat.birthDate = birthDate;
        cat.diagnosedDate = diagnosedDate;
        cat.mealCount = mealCount;
        cat.bloodSugarCount = bloodSugarCount;
        cat.insulinCount = insulinCount;
        // inviteCode는 별도 초대코드 발급 API에서 설정 예정
        return cat;
    }

    public void assignInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public void updateMealCount(int mealCount) {
        this.mealCount = mealCount;
    }

    public void updateBloodSugarCount(int bloodSugarCount) {
        this.bloodSugarCount = bloodSugarCount;
    }

    public void updateInsulinCount(int insulinCount) {
        this.insulinCount = insulinCount;
    }

    // 고양이 기본 정보 수정 (PATCH /api/v1/cats/me)
    // birthDate는 null 허용 (미입력 시 null 저장)
    public void updateInfo(String name, LocalDate birthDate, LocalDate diagnosedDate) {
        this.name = name;
        this.birthDate = birthDate;
        this.diagnosedDate = diagnosedDate;
    }

}