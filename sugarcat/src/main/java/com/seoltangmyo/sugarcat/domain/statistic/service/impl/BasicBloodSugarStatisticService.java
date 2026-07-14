package com.seoltangmyo.sugarcat.domain.statistic.service.impl;

import com.seoltangmyo.sugarcat.domain.bloodsugar.entity.BloodSugarRecord;
import com.seoltangmyo.sugarcat.domain.bloodsugar.repository.BloodSugarRecordRepository;
import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.statistic.dto.BloodSugarMonthlyStatisticsResponse;
import com.seoltangmyo.sugarcat.domain.statistic.dto.BloodSugarWeeklyStatisticsResponse;
import com.seoltangmyo.sugarcat.domain.statistic.service.BloodSugarStatisticService;
import com.seoltangmyo.sugarcat.domain.user.entity.User;
import com.seoltangmyo.sugarcat.domain.user.repository.UserRepository;
import com.seoltangmyo.sugarcat.global.error.BusinessException;
import com.seoltangmyo.sugarcat.global.error.ErrorCode;
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
    private static final String MONTHLY = "monthly";

    private final UserRepository userRepository;
    private final BloodSugarRecordRepository bloodSugarRecordRepository;

    @Override
    public BloodSugarWeeklyStatisticsResponse getWeeklyStatistics(
            UUID userId,
            String period,
            LocalDate startDate
    ) {
        validateWeeklyPeriod(period);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Cat cat = user.getCat();
        if (cat == null) {
            throw new BusinessException(ErrorCode.CAT_NOT_FOUND);
        }

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
        DailyBloodSugarStatistics statistics = calculateStatistics(records);

        return new BloodSugarWeeklyStatisticsResponse.Record(
                convertDayOfWeek(date),
                statistics.avg(),
                statistics.min(),
                statistics.max(),
                statistics.count()
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

    private void validateWeeklyPeriod(String period) {
        if (!WEEKLY.equalsIgnoreCase(period)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "period는 weekly만 가능합니다.");
        }
    }


    @Override
    public BloodSugarMonthlyStatisticsResponse getMonthlyStatistics(
            UUID userId,
            String period,
            LocalDate date
    ) {
        validateMonthlyPeriod(period);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Cat cat = user.getCat();
        if (cat == null) {
            throw new BusinessException(ErrorCode.CAT_NOT_FOUND);
        }

        LocalDate monthStartDate = date.withDayOfMonth(1);
        LocalDate monthEndDate = date.withDayOfMonth(date.lengthOfMonth());

        List<BloodSugarRecord> records =
                bloodSugarRecordRepository.findAllByCatAndRecordDateBetween(
                        cat,
                        monthStartDate,
                        monthEndDate
                );

        Map<LocalDate, List<BloodSugarRecord>> recordMap =
                records.stream()
                        .collect(Collectors.groupingBy(BloodSugarRecord::getRecordDate));

        List<BloodSugarMonthlyStatisticsResponse.Record> monthlyRecords =
                monthStartDate.datesUntil(monthEndDate.plusDays(1))
                        .map(currentDate -> createMonthlyDailyStatistics(
                                currentDate,
                                recordMap.getOrDefault(currentDate, List.of())
                        ))
                        .toList();

        return new BloodSugarMonthlyStatisticsResponse(
                "MONTHLY",
                monthStartDate.getYear(),
                monthStartDate.getMonthValue(),
                monthStartDate,
                monthEndDate,
                monthlyRecords
        );
    }

    private BloodSugarMonthlyStatisticsResponse.Record createMonthlyDailyStatistics(
            LocalDate date,
            List<BloodSugarRecord> records
    ) {
        DailyBloodSugarStatistics statistics = calculateStatistics(records);

        return new BloodSugarMonthlyStatisticsResponse.Record(
                date,
                date.getDayOfMonth(),
                statistics.avg(),
                statistics.min(),
                statistics.max(),
                statistics.count()
        );
    }

    private void validateMonthlyPeriod(String period) {
        if (!MONTHLY.equalsIgnoreCase(period)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "period는 monthly만 가능합니다.");
        }
    }

    // 공통 계산 메서드
    private DailyBloodSugarStatistics calculateStatistics(List<BloodSugarRecord> records) {
        if (records.isEmpty()) {
            return new DailyBloodSugarStatistics(null, null, null, 0);
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

        return new DailyBloodSugarStatistics(
                avg,
                min,
                max,
                records.size()
        );
    }

    private record DailyBloodSugarStatistics(
            Integer avg,
            Integer min,
            Integer max,
            int count
    ) {
    }
}
