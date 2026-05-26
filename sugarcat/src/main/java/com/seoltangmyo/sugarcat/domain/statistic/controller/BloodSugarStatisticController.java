package com.seoltangmyo.sugarcat.domain.statistic.controller;

import com.seoltangmyo.sugarcat.domain.statistic.dto.BloodSugarWeeklyStatisticsResponse;
import com.seoltangmyo.sugarcat.domain.statistic.service.BloodSugarStatisticService;
import com.seoltangmyo.sugarcat.global.security.CustomUserDetails;
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

@RestController
@RequestMapping("/api/v1/blood-sugar-statistics")
@RequiredArgsConstructor
public class BloodSugarStatisticController {

    private final BloodSugarStatisticService bloodSugarStatisticService;

    @GetMapping("/me/weekly")
    public ResponseEntity<BloodSugarWeeklyStatisticsResponse> getWeeklyStatistics(
            @AuthenticationPrincipal CustomUserDetails userDetails,
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
}
