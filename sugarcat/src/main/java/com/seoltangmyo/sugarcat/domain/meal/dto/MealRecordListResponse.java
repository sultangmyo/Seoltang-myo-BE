package com.seoltangmyo.sugarcat.domain.meal.dto;

import java.util.List;

// GET /api/v1/meals/me?date={date} 응답
public record MealRecordListResponse(
        List<MealRecordResponse> records
) {
}
