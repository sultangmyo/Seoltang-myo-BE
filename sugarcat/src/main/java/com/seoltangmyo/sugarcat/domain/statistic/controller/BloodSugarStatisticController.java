package com.seoltangmyo.sugarcat.domain.statistic.controller;

import com.seoltangmyo.sugarcat.domain.statistic.dto.BloodSugarMonthlyStatisticsResponse;
import com.seoltangmyo.sugarcat.domain.statistic.dto.BloodSugarWeeklyStatisticsResponse;
import com.seoltangmyo.sugarcat.domain.statistic.service.BloodSugarStatisticService;
import com.seoltangmyo.sugarcat.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@Tag(name = "Statistic", description = "혈당 통계 API")
@RestController
@RequestMapping("/api/v1/blood-sugar-statistics")
@RequiredArgsConstructor
public class BloodSugarStatisticController {

    private final BloodSugarStatisticService bloodSugarStatisticService;

    @Operation(summary = "혈당 주간 통계 조회")
    @GetMapping("/me/weekly")
    public ResponseEntity<BloodSugarWeeklyStatisticsResponse> getWeeklyStatistics(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("period") String period,

            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        UUID userId = userDetails.getUserId();

        BloodSugarWeeklyStatisticsResponse response =
                bloodSugarStatisticService.getWeeklyStatistics(userId, period, date);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "혈당 월간 통계 조회")
    @GetMapping("/me/monthly")
    public ResponseEntity<BloodSugarMonthlyStatisticsResponse> getMonthlyStatistics(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("period") String period,

            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        UUID userId = userDetails.getUserId();

        BloodSugarMonthlyStatisticsResponse response =
                bloodSugarStatisticService.getMonthlyStatistics(userId, period, date);

        return ResponseEntity.ok(response);
    }
}
