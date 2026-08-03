package com.seoltangmyo.sugarcat.domain.bloodsugar.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record BloodSugarRecordUpdateRequest(
        @NotNull(message = "기록 날짜는 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate recordedDate,
        @NotNull(message = "기록 시간은 필수입니다.")
        @JsonFormat(pattern = "HH:mm") LocalTime recordedTime,
        @Min(value = 1, message = "회차는 1 이상이어야 합니다.")
        int sequence,
        @Min(value = 0, message = "혈당 수치는 0 이상이어야 합니다.")
        int sugarValue
) {
}
