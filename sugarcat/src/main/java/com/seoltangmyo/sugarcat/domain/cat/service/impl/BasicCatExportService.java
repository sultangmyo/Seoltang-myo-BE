package com.seoltangmyo.sugarcat.domain.cat.service.impl;

import com.seoltangmyo.sugarcat.domain.bloodsugar.entity.BloodSugarRecord;
import com.seoltangmyo.sugarcat.domain.bloodsugar.repository.BloodSugarRecordRepository;
import com.seoltangmyo.sugarcat.domain.cat.dto.CatExportResponse;
import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.cat.service.CatExportService;
import com.seoltangmyo.sugarcat.domain.insulin.entity.InsulinRecord;
import com.seoltangmyo.sugarcat.domain.insulin.repository.InsulinRecordRepository;
import com.seoltangmyo.sugarcat.domain.meal.entity.MealRecord;
import com.seoltangmyo.sugarcat.domain.meal.repository.MealRecordRepository;
import com.seoltangmyo.sugarcat.domain.user.entity.User;
import com.seoltangmyo.sugarcat.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 서비스 자체가 pdf 데이터 조회 로직
public class BasicCatExportService implements CatExportService {

    private final BloodSugarRecordRepository bloodSugarRecordRepository;
    private final MealRecordRepository mealRecordRepository;
    private final InsulinRecordRepository insulinRecordRepository;
    private final UserRepository userRepository;

    @Override
    public CatExportResponse exportRecords(
            UUID userId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        validateDateRange(startDate, endDate);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cat cat = user.getCat();

        int insulinCount = cat.getInsulinCount();

        // 기록 조회함
        List<BloodSugarRecord> bloodSugarRecords =
                bloodSugarRecordRepository.findAllByCatAndRecordDateBetween(
                        cat,
                        startDate,
                        endDate
                );

        List<MealRecord> mealRecords =
                mealRecordRepository.findAllByCatAndRecordDateBetween(
                        cat,
                        startDate,
                        endDate
                );

        List<InsulinRecord> insulinRecords =
                insulinRecordRepository.findAllByCatAndRecordDateBetween(
                        cat,
                        startDate,
                        endDate
                );

        // 날짜별 묶음
        Map<LocalDate, List<BloodSugarRecord>> bloodSugarMap =
                bloodSugarRecords.stream()
                        .collect(Collectors.groupingBy(BloodSugarRecord::getRecordDate));

        Map<LocalDate, List<MealRecord>> mealMap =
                mealRecords.stream()
                        .collect(Collectors.groupingBy(MealRecord::getRecordDate));

        Map<LocalDate, List<InsulinRecord>> insulinMap =
                insulinRecords.stream()
                        .collect(Collectors.groupingBy(InsulinRecord::getRecordDate));

        // 시작일 ~ 종료일 날짜 순회
        List<CatExportResponse.Row> rows = startDate.datesUntil(endDate.plusDays(1))
                .map(date -> createRow( // 날짜별 Row 생성
                        date,
                        bloodSugarMap.getOrDefault(date, List.of()), // 기록 있으면 가져오고, 없으면 빈 리스트 반환
                        mealMap.getOrDefault(date, List.of()),
                        insulinMap.getOrDefault(date, List.of()),
                        insulinCount
                ))
                .toList();

        return new CatExportResponse(rows);
    }

    private CatExportResponse.Row createRow(
            LocalDate date,
            List<BloodSugarRecord> bloodSugarRecords,
            List<MealRecord> mealRecords,
            List<InsulinRecord> insulinRecords,
            int insulinCount
    ) {
        List<CatExportResponse.BloodSugar> bloodSugars =
                bloodSugarRecords.stream()
                        .sorted(Comparator.comparing(BloodSugarRecord::getRecordTime))
                        .map(record -> new CatExportResponse.BloodSugar(
                                record.getRecordTime(),
                                record.getSugarValue(),
                                record.getSugarStatus()
                        ))
                        .toList();

        List<CatExportResponse.Meal> meals =
                mealRecords.stream()
                        .sorted(Comparator.comparing(MealRecord::getRecordTime))
                        .map(record -> new CatExportResponse.Meal(
                                record.getRecordTime(),
                                record.getMealStatus()
                        ))
                        .toList();

        // 인슐린 누락 계산
        List<Integer> missedIndexes =
                calculateMissedIndexes(insulinRecords, insulinCount);

        return new CatExportResponse.Row(
                date,
                bloodSugars,
                meals,
                new CatExportResponse.Insulin(missedIndexes)
        );
    }

    private List<Integer> calculateMissedIndexes(
            List<InsulinRecord> insulinRecords,
            int insulinCount
    ) {
        Set<Integer> savedSequences = insulinRecords.stream()
                .filter(InsulinRecord::isInjected)
                .map(InsulinRecord::getSequence)
                .collect(Collectors.toSet());

        // 1 ~ insulinCount까지 반복
        return IntStream.rangeClosed(1, insulinCount)
                .filter(sequence -> !savedSequences.contains(sequence))
                .boxed()
                .toList();
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작 날짜는 종료 날짜보다 늦을 수 없습니다.");
        }
    }
}
