package com.seoltangmyo.sugarcat.domain.meal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

// POST /api/v1/meals/me 요청 바디
public record MealRecordCreateRequest(
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        int sequence,
        @JsonFormat(pattern = "HH:mm") LocalTime recordTime,
        String mealStatus   // FULL / PARTIAL
) {
}
