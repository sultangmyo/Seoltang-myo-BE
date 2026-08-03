package com.seoltangmyo.sugarcat.domain.bloodsugar.service;

import com.seoltangmyo.sugarcat.domain.bloodsugar.dto.BloodSugarRecordListResponse;
import com.seoltangmyo.sugarcat.domain.bloodsugar.dto.BloodSugarRecordResponse;
import com.seoltangmyo.sugarcat.domain.bloodsugar.entity.BloodSugarRecord;
import com.seoltangmyo.sugarcat.domain.bloodsugar.repository.BloodSugarRecordRepository;
import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.cat.repository.CatRepository;
import com.seoltangmyo.sugarcat.global.error.BusinessException;
import com.seoltangmyo.sugarcat.global.error.ErrorCode;
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
public class BloodSugarRecordQueryService {

    private final CatRepository catRepository;
    private final BloodSugarRecordRepository bloodSugarRecordRepository;

    @Cacheable(
            cacheNames = "dailyBloodSugarRecords",
            key = "{#catId, #date}"
    )
    @Transactional(readOnly = true)
    public BloodSugarRecordListResponse getBloodSugarRecordsByDate(
            UUID catId,
            LocalDate date
    ) {
        log.info("[혈당 기록 조회 - 캐시 미스] catId={}, date={}", catId, date);

        Cat cat = catRepository.findById(catId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CAT_NOT_FOUND));

        List<BloodSugarRecord> records =
                bloodSugarRecordRepository.findAllByCatAndRecordDateOrderBySequenceAsc(cat, date);

        List<BloodSugarRecordResponse> recordResponses = records.stream()
                .map(record -> new BloodSugarRecordResponse(
                        record.getRecordedBy() != null
                                ? record.getRecordedBy().getNickname()
                                : "탈퇴한 사용자",
                        record.getRecordTime(),
                        record.getSequence(),
                        record.getSugarValue(),
                        record.getSugarStatus()
                ))
                .toList();

        return new BloodSugarRecordListResponse(recordResponses);
    }
}
