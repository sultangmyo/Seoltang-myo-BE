package com.seoltangmyo.sugarcat.domain.statistic.dto;

import java.time.LocalDate;
import java.util.List;

public record BloodSugarWeeklyStatisticsResponse(
        String period,
        LocalDate startDate,
        LocalDate endDate,
        List<Record> records
) {
    public record Record(
            String dayOfWeek,
            Integer avg,
            Integer min,
            Integer max,
            int count
    ) {
    }
}
