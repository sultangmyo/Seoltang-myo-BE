package com.seoltangmyo.sugarcat.domain.statistic.service.impl;

import com.seoltangmyo.sugarcat.domain.bloodsugar.entity.BloodSugarRecord;
import com.seoltangmyo.sugarcat.domain.bloodsugar.repository.BloodSugarRecordRepository;
import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.statistic.dto.BloodSugarWeeklyStatisticsResponse;
import com.seoltangmyo.sugarcat.domain.statistic.service.BloodSugarStatisticService;
import com.seoltangmyo.sugarcat.domain.user.entity.User;
import com.seoltangmyo.sugarcat.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicBloodSugarStatisticService implements BloodSugarStatisticService {

    private static final String WEEKLY = "weekly";

    private final UserRepository userRepository;
    private final BloodSugarRecordRepository bloodSugarRecordRepository;

    @Override
    public BloodSugarWeeklyStatisticsResponse getWeeklyStatistics(
            UUID userId,
            String period,
            LocalDate date
    ) {
        validatePeriod(period);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cat cat = user.getCat();

        LocalDate startDate = date;
        LocalDate endDate = startDate.plusDays(6);

        List<BloodSugarRecord> records =
                bloodSugarRecordRepository.findAllByCatAndRecordDateBetween(
                        cat,
                        startDate,
                        endDate
                );

        Map<LocalDate, List<BloodSugarRecord>> recordMap =
                records.stream()
                        .collect(Collectors.groupingBy(BloodSugarRecord::getRecordDate));

        List<BloodSugarWeeklyStatisticsResponse.Record> weeklyRecords =
                startDate.datesUntil(endDate.plusDays(1))
                        .map(currentDate -> createDailyStatistics(
                                currentDate,
                                recordMap.getOrDefault(currentDate, List.of())
                        ))
                        .toList();

        return new BloodSugarWeeklyStatisticsResponse(
                "WEEKLY",
                startDate,
                endDate,
                weeklyRecords
        );
    }

    private BloodSugarWeeklyStatisticsResponse.Record createDailyStatistics(
            LocalDate date,
            List<BloodSugarRecord> records
    ) {
        if (records.isEmpty()) {
            return new BloodSugarWeeklyStatisticsResponse.Record(
                    convertDayOfWeek(date),
                    null,
                    null,
                    null,
                    0
            );
        }

        int avg = (int) Math.round(
                records.stream()
                        .mapToInt(BloodSugarRecord::getSugarValue)
                        .average()
                        .orElse(0)
        );

        int min = records.stream()
                .mapToInt(BloodSugarRecord::getSugarValue)
                .min()
                .orElse(0);

        int max = records.stream()
                .mapToInt(BloodSugarRecord::getSugarValue)
                .max()
                .orElse(0);

        int count = records.size();

        return new BloodSugarWeeklyStatisticsResponse.Record(
                convertDayOfWeek(date),
                avg,
                min,
                max,
                count
        );
    }

    private String convertDayOfWeek(LocalDate date) {
        return switch (date.getDayOfWeek()) {
            case MONDAY -> "MON";
            case TUESDAY -> "TUE";
            case WEDNESDAY -> "WED";
            case THURSDAY -> "THU";
            case FRIDAY -> "FRI";
            case SATURDAY -> "SAT";
            case SUNDAY -> "SUN";
        };
    }

    private void validatePeriod(String period) {
        if (!WEEKLY.equalsIgnoreCase(period)) {
            throw new IllegalArgumentException("period는 weekly만 가능합니다.");
        }
    }
}
