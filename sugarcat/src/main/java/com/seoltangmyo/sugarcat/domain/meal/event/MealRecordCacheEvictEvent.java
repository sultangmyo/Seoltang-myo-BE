package com.seoltangmyo.sugarcat.domain.meal.event;

import java.time.LocalDate;
import java.util.UUID;

public record MealRecordCacheEvictEvent(
        UUID catId,
        LocalDate recordDate
) {
}
