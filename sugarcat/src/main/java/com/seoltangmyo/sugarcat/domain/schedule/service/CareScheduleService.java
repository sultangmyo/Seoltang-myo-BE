package com.seoltangmyo.sugarcat.domain.schedule.service;

import com.seoltangmyo.sugarcat.domain.cache.CatCacheEvictService;
import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.schedule.dto.CareScheduleInfoResponse;
import com.seoltangmyo.sugarcat.domain.schedule.dto.CareScheduleUpdateRequest;
import com.seoltangmyo.sugarcat.domain.schedule.entity.CareSchedule;
import com.seoltangmyo.sugarcat.domain.schedule.entity.CareScheduleType;
import com.seoltangmyo.sugarcat.domain.schedule.repository.CareScheduleRepository;
import com.seoltangmyo.sugarcat.domain.user.dto.MessageResponse;
import com.seoltangmyo.sugarcat.domain.user.entity.User;
import com.seoltangmyo.sugarcat.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CareScheduleService {

    private final CareScheduleRepository careScheduleRepository;
    private final UserRepository userRepository;
    private final CareScheduleQueryService careScheduleQueryService;
    private final CatCacheEvictService catCacheEvictService;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // 케어 스케줄 조회 (식사 / 혈당 / 인슐린 공용)
    @Transactional
    public CareScheduleInfoResponse getSchedules(UUID userId, CareScheduleType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cat cat = user.getCat();

        if (cat == null) {
            throw new IllegalArgumentException("등록된 고양이가 없습니다.");
        }

        return careScheduleQueryService.getSchedules(
                cat.getId(),
                type
        );
    }

    // 케어 스케줄 수정 (식사 / 혈당 / 인슐린 공용)
    // 기존 스케줄 전체 삭제 후 새로 저장
    @Transactional
    public MessageResponse updateSchedules(UUID userId, CareScheduleType type, CareScheduleUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cat cat = user.getCat();
        if (cat == null) {
            throw new IllegalArgumentException("등록된 고양이가 없습니다.");
        }

        // cat count 업데이트
        switch (type) {
            case MEAL -> cat.updateMealCount(request.count());
            case BLOODSUGAR -> cat.updateBloodSugarCount(request.count());
            case INSULIN -> cat.updateInsulinCount(request.count());
        }

        // 기존 스케줄 전체 삭제 후 새로 저장
        careScheduleRepository.deleteAllByCatAndScheduleType(cat, type);

        for (CareScheduleUpdateRequest.ScheduleItem item : request.schedules()) {
            LocalTime time = LocalTime.parse(item.time(), TIME_FORMATTER);
            careScheduleRepository.save(CareSchedule.create(cat, type, item.sequence(), time));
        }

        String message = switch (type) {
            case MEAL -> "식사 관리 설정이 수정되었습니다.";
            case BLOODSUGAR -> "혈당 체크 설정이 수정되었습니다.";
            case INSULIN -> "인슐린 관리 설정이 수정되었습니다.";
        };

        catCacheEvictService.evictCareSchedules(
                cat.getId(),
                type
        );

        return new MessageResponse(message);
    }

}
