package com.seoltangmyo.sugarcat.domain.bloodsugar.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;

public record BloodSugarRecordCreateRequest(
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate recordedDate,
        @JsonFormat(pattern = "HH:mm") LocalTime recordedTime,
        int sequence,
        int sugarValue
) {
}
