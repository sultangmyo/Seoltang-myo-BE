package com.seoltangmyo.sugarcat.domain.meal.event;

import java.util.UUID;

public record MealRecordCreatedEvent(
        UUID recordId
) {
}
