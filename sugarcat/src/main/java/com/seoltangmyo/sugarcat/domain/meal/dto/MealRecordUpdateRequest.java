package com.seoltangmyo.sugarcat.domain.meal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

// PATCH /api/v1/meals/me 요청 바디
// date + sequence로 기존 기록을 조회하여 recordTime, mealStatus를 수정
public record MealRecordUpdateRequest(
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        int sequence,
        @JsonFormat(pattern = "HH:mm") LocalTime recordTime,
        String mealStatus  // FULL / PARTIAL
) {
}
