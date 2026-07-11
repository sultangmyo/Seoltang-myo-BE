package com.seoltangmyo.sugarcat.domain.meal.service;

import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.cat.repository.CatRepository;
import com.seoltangmyo.sugarcat.domain.meal.dto.MealRecordListResponse;
import com.seoltangmyo.sugarcat.domain.meal.dto.MealRecordResponse;
import com.seoltangmyo.sugarcat.domain.meal.entity.MealRecord;
import com.seoltangmyo.sugarcat.domain.meal.repository.MealRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MealRecordQueryService {

    private final CatRepository catRepository;
    private final MealRecordRepository mealRecordRepository;

    @Cacheable(
            cacheNames = "dailyMealRecords",
            key = "{#catId, #date}"
    )
    @Transactional(readOnly = true)
    public MealRecordListResponse getMealRecords(
            UUID catId,
            LocalDate date
    ) {
        log.info("[식사 기록 조회 - 캐시 미스] catId={}, date={}", catId, date);

        Cat cat = catRepository.findById(catId)
                .orElseThrow(() -> new IllegalArgumentException("고양이를 찾을 수 없습니다."));

        List<MealRecord> records =
                mealRecordRepository.findAllByCatAndRecordDateOrderBySequenceAsc(cat, date);

        List<MealRecordResponse> responses = records.stream()
                .map(record -> new MealRecordResponse(
                        record.getSequence(),
                        record.getRecordTime(),
                        record.getMealStatus().name(),
                        record.getRecordedBy() != null
                                ? record.getRecordedBy().getNickname()
                                : "탈퇴한 사용자"
                ))
                .toList();

        return new MealRecordListResponse(responses);
    }
}