package com.seoltangmyo.sugarcat.domain.schedule.dto;

import java.util.List;

// PATCH /api/v1/care_schedules/me/{type} 요청 바디
public record CareScheduleUpdateRequest(
        int count,
        List<ScheduleItem> schedules
) {
    public record ScheduleItem(
            int sequence,
            String time   // HH:mm 형식
    ) {}
}
