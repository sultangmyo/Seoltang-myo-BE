package com.seoltangmyo.sugarcat.domain.meal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;

// GET /api/v1/meals/me 개별 기록 항목
public record MealRecordResponse(
        int sequence,
        @JsonFormat(pattern = "HH:mm") LocalTime recordTime,
        String mealStatus,
        String nickname     // null이면 "탈퇴한 사용자" 반환
) {
}
