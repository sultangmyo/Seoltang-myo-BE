package com.seoltangmyo.sugarcat.domain.insulin.event;

import java.util.UUID;

public record InsulinRecordCreatedEvent(
        UUID recordId
) {
}
