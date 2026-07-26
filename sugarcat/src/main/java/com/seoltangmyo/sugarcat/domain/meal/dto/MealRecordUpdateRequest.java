package com.seoltangmyo.sugarcat.domain.meal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.LocalTime;

// PATCH /api/v1/meals/me 요청 바디
// date + sequence로 기존 기록을 조회하여 recordTime, mealStatus를 수정
public record MealRecordUpdateRequest(
        @NotNull(message = "기록 날짜는 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @Min(value = 1, message = "회차는 1 이상이어야 합니다.")
        int sequence,
        @NotNull(message = "기록 시간은 필수입니다.")
        @JsonFormat(pattern = "HH:mm") LocalTime recordTime,
        @NotBlank(message = "식사 상태는 필수입니다.")
        @Pattern(regexp = "FULL|PARTIAL", message = "식사 상태는 FULL 또는 PARTIAL이어야 합니다.")
        String mealStatus  // FULL / PARTIAL
) {
}
