package com.seoltangmyo.sugarcat.domain.statistic.service;

import com.seoltangmyo.sugarcat.domain.statistic.dto.BloodSugarMonthlyStatisticsResponse;
import com.seoltangmyo.sugarcat.domain.statistic.dto.BloodSugarWeeklyStatisticsResponse;

import java.time.LocalDate;
import java.util.UUID;

public interface BloodSugarStatisticService {

    BloodSugarWeeklyStatisticsResponse getWeeklyStatistics(
            UUID userId,
            String period,
            LocalDate date
    );

    BloodSugarMonthlyStatisticsResponse getMonthlyStatistics(
            UUID userId,
            String period,
            LocalDate date
    );
}
