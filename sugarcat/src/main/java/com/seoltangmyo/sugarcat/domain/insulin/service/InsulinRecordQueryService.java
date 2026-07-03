package com.seoltangmyo.sugarcat.domain.insulin.service;

import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.cat.repository.CatRepository;
import com.seoltangmyo.sugarcat.domain.insulin.dto.InsulinRecordListResponse;
import com.seoltangmyo.sugarcat.domain.insulin.entity.InsulinRecord;
import com.seoltangmyo.sugarcat.domain.insulin.repository.InsulinRecordRepository;
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
public class InsulinRecordQueryService {

    private final CatRepository catRepository;
    private final InsulinRecordRepository insulinRecordRepository;

    @Cacheable(
            cacheNames = "dailyInsulinRecords",
            key = "{#catId, #date}"
    )
    @Transactional(readOnly = true)
    public InsulinRecordListResponse getInsulinRecords(
            UUID catId,
            LocalDate date
    ) {
        log.info("[인슐린 기록 조회 - 캐시 미스] catId={}, date={}", catId, date);

        Cat cat = catRepository.findById(catId)
                .orElseThrow(() -> new IllegalArgumentException("고양이를 찾을 수 없습니다."));

        List<InsulinRecord> records =
                insulinRecordRepository.findAllByCatAndRecordDateOrderBySequenceAsc(cat, date);

        List<InsulinRecordListResponse.InsulinRecordItem> items = records.stream()
                .map(record -> new InsulinRecordListResponse.InsulinRecordItem(
                        record.getSequence(),
                        record.isInjected(),
                        record.getRecordedBy() != null
                                ? record.getRecordedBy().getNickname()
                                : "탈퇴한 사용자"

                ))
                .toList();

        return new InsulinRecordListResponse(items);
    }
}
