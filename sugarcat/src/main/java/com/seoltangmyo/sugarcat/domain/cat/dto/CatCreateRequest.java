package com.seoltangmyo.sugarcat.domain.cat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

// POST /api/v1/cats 요청 바디
// 고양이 기본 정보 + 식사/혈당/인슐린 루틴을 한 번에 저장
public record CatCreateRequest(
        @Valid @NotNull(message = "고양이 정보는 필수입니다.") CatInfo cat,          // 고양이 기본 정보
        @Valid @NotNull(message = "식사 루틴 설정은 필수입니다.") ScheduleGroup meal,       // 식사 루틴 설정
        @Valid @NotNull(message = "혈당 체크 루틴 설정은 필수입니다.") ScheduleGroup bloodSugar, // 혈당 체크 루틴 설정
        @Valid @NotNull(message = "인슐린 루틴 설정은 필수입니다.") ScheduleGroup insulin     // 인슐린 루틴 설정
) {

    // 고양이 기본 정보
    public record CatInfo(
            @NotBlank(message = "고양이 이름은 필수입니다.")
            @Size(max = 10, message = "고양이 이름은 최대 10자까지 입력할 수 있습니다.")
            String name,           // 고양이 이름 (최대 10자)
            @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthDate,   // 생년월일 (null 허용 - 프론트에서 "모르겠어요" 선택 시 null)
            @NotNull(message = "당뇨 진단일은 필수입니다.")
            @JsonFormat(pattern = "yyyy-MM-dd") LocalDate diagnosedDate, // 당뇨 진단일 (필수)
            @Min(value = 1, message = "식사 횟수는 1 이상이어야 합니다.") int mealCount,         // 식사 횟수
            @Min(value = 1, message = "혈당 체크 횟수는 1 이상이어야 합니다.") int bloodSugarCount,   // 혈당 체크 횟수
            @Min(value = 1, message = "인슐린 투여 횟수는 1 이상이어야 합니다.") int insulinCount       // 인슐린 투여 횟수
    ) {
    }

    // 루틴 스케줄 그룹 (식사/혈당/인슐린 공용)
    public record ScheduleGroup(
            @Valid @NotNull(message = "스케줄 목록은 필수입니다.")
            List<ScheduleItem> schedules  // 시간 목록 (빈 배열 허용 → row 미생성)
    ) {
    }

    // 개별 스케줄 항목
    public record ScheduleItem(
            @Min(value = 1, message = "회차는 1 이상이어야 합니다.")
            int sequence,  // 회차 (1, 2, 3 …) - 프론트에서 전달한 값 그대로 저장
            @Pattern(regexp = "^$|^([01]\\d|2[0-3]):[0-5]\\d$", message = "시간은 HH:mm 형식이어야 합니다.")
            String time    // 시간 (HH:mm 형식, null 허용 -> 저장하지 않음)
    ) {
    }
}
