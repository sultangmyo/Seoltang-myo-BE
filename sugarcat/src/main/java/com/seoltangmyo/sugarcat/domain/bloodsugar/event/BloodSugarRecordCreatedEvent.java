package com.seoltangmyo.sugarcat.domain.bloodsugar.event;

import java.util.UUID;

public record BloodSugarRecordCreatedEvent(
        UUID recordId
) {
}
