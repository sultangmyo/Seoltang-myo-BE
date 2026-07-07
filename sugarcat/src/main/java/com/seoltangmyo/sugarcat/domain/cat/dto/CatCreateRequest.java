package com.seoltangmyo.sugarcat.domain.cat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.List;

// POST /api/v1/cats 요청 바디
// 고양이 기본 정보 + 식사/혈당/인슐린 루틴을 한 번에 저장
public record CatCreateRequest(
        CatInfo cat,          // 고양이 기본 정보
        ScheduleGroup meal,       // 식사 루틴 설정
         ScheduleGroup bloodSugar, // 혈당 체크 루틴 설정
        ScheduleGroup insulin     // 인슐린 루틴 설정
) {

    // 고양이 기본 정보
    public record CatInfo(
            String name,           // 고양이 이름 (최대 10자)
            @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthDate,   // 생년월일 (null 허용 - 프론트에서 "모르겠어요" 선택 시 null)
            @JsonFormat(pattern = "yyyy-MM-dd") LocalDate diagnosedDate, // 당뇨 진단일 (필수)
            int mealCount,         // 식사 횟수
            int bloodSugarCount,   // 혈당 체크 횟수
            int insulinCount       // 인슐린 투여 횟수
    ) {
    }

    // 루틴 스케줄 그룹 (식사/혈당/인슐린 공용)
    public record ScheduleGroup(List<ScheduleItem> schedules  // 시간 목록 (빈 배열 허용 → row 미생성)
    ) {
    }

    // 개별 스케줄 항목
    public record ScheduleItem(
            int sequence,  // 회차 (1, 2, 3 …) - 프론트에서 전달한 값 그대로 저장
            String time    // 시간 (HH:mm 형식, null 허용 -> 저장하지 않음)
    ) {
    }
}
