package com.seoltangmyo.sugarcat.domain.statistic.dto;

import java.time.LocalDate;
import java.util.List;

public record BloodSugarMonthlyStatisticsResponse(
        String period,
        int year,
        int month,
        LocalDate startDate,
        LocalDate endDate,
        List<Record> records
) {
    public record Record(
            LocalDate date,
            int day,
            Integer avg,
            Integer min,
            Integer max,
            int count
    ){
    }
}
