package com.seoltangmyo.sugarcat.domain.bloodsugar.event;

import java.time.LocalDate;
import java.util.UUID;

public record BloodSugarRecordCacheEvictEvent(
        UUID catId,
        LocalDate recordDate
) {
}
