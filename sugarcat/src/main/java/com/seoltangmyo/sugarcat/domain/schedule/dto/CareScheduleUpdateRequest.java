package com.seoltangmyo.sugarcat.domain.schedule.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

// PATCH /api/v1/care_schedules/me/{type} 요청 바디
public record CareScheduleUpdateRequest(
        @Min(value = 1, message = "횟수는 1 이상이어야 합니다.")
        int count,
        @Valid @NotNull(message = "스케줄 목록은 필수입니다.")
        List<ScheduleItem> schedules
) {
    public record ScheduleItem(
            @Min(value = 1, message = "회차는 1 이상이어야 합니다.")
            int sequence,
            @Pattern(regexp = "^$|^([01]\\d|2[0-3]):[0-5]\\d$", message = "시간은 HH:mm 형식이어야 합니다.")
            String time   // HH:mm 형식
    ) {}
}
