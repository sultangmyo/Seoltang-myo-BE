package com.seoltangmyo.sugarcat.domain.insulin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

// POST /api/v1/insulin-records/me 요청 바디
public record InsulinRecordCreateRequest(
        boolean isInjected,
        @Min(value = 1, message = "회차는 1 이상이어야 합니다.")
        int sequence,
        @NotNull(message = "기록 날짜는 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate recordDate
) {
}
