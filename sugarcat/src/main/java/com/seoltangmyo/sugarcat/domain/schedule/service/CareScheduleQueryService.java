package com.seoltangmyo.sugarcat.domain.schedule.service;

import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.cat.repository.CatRepository;
import com.seoltangmyo.sugarcat.domain.schedule.dto.CareScheduleInfoResponse;
import com.seoltangmyo.sugarcat.domain.schedule.entity.CareSchedule;
import com.seoltangmyo.sugarcat.domain.schedule.entity.CareScheduleType;
import com.seoltangmyo.sugarcat.domain.schedule.repository.CareScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CareScheduleQueryService {

    private final CatRepository catRepository;
    private final CareScheduleRepository careScheduleRepository;

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    @Cacheable(
            cacheNames = "careSchedules",
            key = "{#catId, #type}"
    )
    @Transactional(readOnly = true)
    public CareScheduleInfoResponse getSchedules(
            UUID catId,
            CareScheduleType type
    ) {
        log.info("[케어 스케줄 조회 - 캐시 미스] catId={}, type={}", catId, type);

        Cat cat = catRepository.findById(catId)
                .orElseThrow(() -> new IllegalArgumentException("고양이를 찾을 수 없습니다."));

        List<CareSchedule> schedules =
                careScheduleRepository.findAllByCatAndScheduleTypeOrderBySequenceAsc(
                        cat,
                        type
                );

        int count = switch (type) {
            case MEAL -> cat.getMealCount();
            case BLOODSUGAR -> cat.getBloodSugarCount();
            case INSULIN -> cat.getInsulinCount();
        };

        List<CareScheduleInfoResponse.ScheduleItem> items = schedules.stream()
                .map(schedule -> new CareScheduleInfoResponse.ScheduleItem(
                        schedule.getSequence(),
                        schedule.getScheduledTime().format(TIME_FORMATTER)
                ))
                .toList();

        return new CareScheduleInfoResponse(count, items);
    }

}