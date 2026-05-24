package com.seoltangmyo.sugarcat.domain.cat.service;

import com.seoltangmyo.sugarcat.domain.cat.dto.CatCreateRequest;
import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.cat.repository.CatRepository;
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
public class CatService {

    private final CatRepository catRepository;
    private final CareScheduleRepository careScheduleRepository;
    private final UserRepository userRepository;

    // 스케줄 시간 파싱 포맷 (프론트에서 "HH:mm" 형식으로 전달)
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // 고양이 신규 등록
    // 1. 고양이 기본 정보 저장
    // 2. 현재 사용자의 catId를 새로 생성된 고양이로 연결
    // 3. 식사/혈당/인슐린 루틴 각각 저장 (schedules 빈 배열이면 해당 타입 row 미생성)
    @Transactional
    public MessageResponse createCat(UUID userId, CatCreateRequest request) {
        // 1. 고양이 생성 및 저장
        Cat cat = Cat.create(
                request.cat().name(),
                request.cat().birthDate(),
                request.cat().diagnosedDate(),
                request.cat().mealCount(),
                request.cat().bloodSugarCount(),
                request.cat().insulinCount()
        );
        catRepository.save(cat);

        // 2. 현재 사용자와 고양이 연결
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.assignCat(cat);

        // 3. 루틴 스케줄 저장
        // schedules가 빈 배열이면 for-each가 실행되지 않아 row 미생성
        saveSchedules(cat, CareScheduleType.MEAL, request.meal().schedules());
        saveSchedules(cat, CareScheduleType.BLOODSUGAR, request.bloodSugar().schedules());
        saveSchedules(cat, CareScheduleType.INSULIN, request.insulin().schedules());

        return new MessageResponse("고양이 기본 정보가 생성되었습니다.");
    }

    // 루틴 스케줄 저장 헬퍼
    // schedules가 빈 배열이면 아무것도 저장하지 않음 (row 미생성)
    // 타입별로 (MEAL / BLOODSUGAR / INSULIN) 각각 호출
    private void saveSchedules(Cat cat, CareScheduleType type, List<CatCreateRequest.ScheduleItem> schedules) {
        for (CatCreateRequest.ScheduleItem item : schedules) {
            // "HH:mm" 문자열 → LocalTime 변환
            LocalTime time = LocalTime.parse(item.time(), TIME_FORMATTER);
            CareSchedule schedule = CareSchedule.create(cat, type, item.sequence(), time);
            careScheduleRepository.save(schedule);
        }
    }
}
