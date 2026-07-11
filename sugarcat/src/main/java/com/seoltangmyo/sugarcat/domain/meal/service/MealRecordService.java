package com.seoltangmyo.sugarcat.domain.meal.service;

import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.meal.dto.MealRecordCreateRequest;
import com.seoltangmyo.sugarcat.domain.meal.dto.MealRecordListResponse;
import com.seoltangmyo.sugarcat.domain.meal.dto.MealRecordUpdateRequest;
import com.seoltangmyo.sugarcat.domain.meal.entity.MealRecord;
import com.seoltangmyo.sugarcat.domain.meal.entity.MealStatus;
import com.seoltangmyo.sugarcat.domain.meal.event.MealRecordCacheEvictEvent;
import com.seoltangmyo.sugarcat.domain.meal.event.MealRecordCreatedEvent;
import com.seoltangmyo.sugarcat.domain.meal.repository.MealRecordRepository;
import com.seoltangmyo.sugarcat.domain.user.dto.MessageResponse;
import com.seoltangmyo.sugarcat.domain.user.entity.User;
import com.seoltangmyo.sugarcat.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MealRecordService {

    private final MealRecordRepository mealRecordRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MealRecordQueryService mealRecordQueryService;

    // 식사 기록 저장
    // POST /api/v1/meals/me
    // 같은 날짜+순번 기록이 이미 있으면 예외 처리
    @Transactional
    public MessageResponse createMealRecord(UUID userId, MealRecordCreateRequest request) {

        LocalDate date = request.date();
        int sequence = request.sequence();

        log.info("[식사 기록 저장] userId={}, date={}, sequence={}, mealStatus={}", userId, request, sequence, request.mealStatus());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cat cat = user.getCat();
        if (cat == null) {
            log.warn("[식사 기록 저장 실패] 고양이 없음 - userId={}", userId);
            throw new IllegalArgumentException("등록된 고양이가 없습니다.");
        }

        boolean exists = mealRecordRepository.existsByCatAndRecordDateAndSequence(cat, date, sequence);
        if (exists) {
            log.warn("[식사 기록 저장 실패] 중복 기록 - catId={}, date={}, sequence={}", cat.getId(), date, sequence);
            throw new IllegalArgumentException("이미 해당 순번의 식사 기록이 존재합니다.");
        }

        MealStatus mealStatus = MealStatus.valueOf(request.mealStatus());

        MealRecord mealRecord = MealRecord.create(cat, user, date, request.recordTime(), sequence, mealStatus);

        mealRecordRepository.save(mealRecord);

        eventPublisher.publishEvent(
                new MealRecordCacheEvictEvent(
                        cat.getId(),
                        date
                )
        );

        log.info("[식사 기록 저장 완료] recordId={}", mealRecord.getId());

        eventPublisher.publishEvent(new MealRecordCreatedEvent(mealRecord.getId()));

        log.info("[식사 기록 저장 이벤트 발행] recordId={}", mealRecord.getId());

        return new MessageResponse("식사 기록이 저장되었습니다.");
    }

    // 날짜별 식사 기록 조회
    // GET /api/v1/meals/me?date={date}
    @Transactional(readOnly = true)
    public MealRecordListResponse getMealRecords(UUID userId, LocalDate date) {
        log.info("[식사 기록 조회] userId={}, date={}", userId, date);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cat cat = user.getCat();

        if (cat == null) {
            log.warn("[식사 기록 조회 실패] 고양이 없음 - userId={}", userId);
            throw new IllegalArgumentException("등록된 고양이가 없습니다.");
        }

        return mealRecordQueryService.getMealRecords(
                cat.getId(),
                date
        );
    }

    // 식사 기록 수정
    // PATCH /api/v1/meals/me
    // date + sequence로 기존 기록을 조회하여 recordTime, mealStatus를 변경 (더티체킹)
    @Transactional
    public MessageResponse updateMealRecord(UUID userId, MealRecordUpdateRequest request) {
        log.info("[식사 기록 수정] userId={}, date={}, sequence={}", userId, request.date(), request.sequence());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cat cat = user.getCat();

        MealRecord mealRecord = mealRecordRepository
                .findByCatAndRecordDateAndSequence(cat, request.date(), request.sequence())
                .orElseThrow(() -> {
                    log.warn("[식사 기록 수정 실패] 기록 없음 - catId={}, date={}, sequence={}",
                            cat.getId(), request.date(), request.sequence());
                    return new IllegalArgumentException("식사 기록을 찾을 수 없습니다.");
                });

        MealStatus mealStatus = MealStatus.valueOf(request.mealStatus());

        // 더티체킹으로 자동 반영
        mealRecord.update(request.recordTime(), mealStatus);

        eventPublisher.publishEvent(
                new MealRecordCacheEvictEvent(
                        cat.getId(),
                        request.date()
                )
        );

        log.info("[식사 기록 수정 완료] recordId={}", mealRecord.getId());

        return new MessageResponse("식사 기록이 수정되었습니다.");
    }
}
