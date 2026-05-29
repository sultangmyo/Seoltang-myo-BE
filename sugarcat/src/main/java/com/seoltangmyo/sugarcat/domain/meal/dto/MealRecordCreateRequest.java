package com.seoltangmyo.sugarcat.domain.meal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;

// POST /api/v1/meals/me 요청 바디 (date, sequence는 쿼리파라미터로 수신)
public record MealRecordCreateRequest(
        @JsonFormat(pattern = "HH:mm") LocalTime recordTime,
        String mealStatus   // FULL / PARTIAL
) {
}
