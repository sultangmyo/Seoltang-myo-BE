package com.seoltangmyo.sugarcat.domain.schedule.dto;

import java.util.List;

// GET /api/v1/care_schedules/me/{type} 응답
public record CareScheduleInfoResponse(
        int count,
        List<ScheduleItem> schedules
) {
    public record ScheduleItem(
            int sequence,
            String time   // HH:mm 형식
    ) {}
}
