package com.seoltangmyo.sugarcat.domain.insulin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

// POST /api/v1/insulin-records/me 요청 바디
public record InsulinRecordCreateRequest(
        boolean isInjected,
        int sequence,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate recordDate
) {
}
