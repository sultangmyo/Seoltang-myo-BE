package com.seoltangmyo.sugarcat.domain.bloodsugar.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record BloodSugarRecordCreateRequest(
        LocalDate recordedDate,
        LocalTime recordedTime,
        int sequence,
        int sugarValue
) {
}
