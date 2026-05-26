package com.seoltangmyo.sugarcat.domain.meal.service;

import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.meal.dto.MealRecordCreateRequest;
import com.seoltangmyo.sugarcat.domain.meal.dto.MealRecordListResponse;
import com.seoltangmyo.sugarcat.domain.meal.dto.MealRecordResponse;
import com.seoltangmyo.sugarcat.domain.meal.entity.MealRecord;
import com.seoltangmyo.sugarcat.domain.meal.entity.MealStatus;
import com.seoltangmyo.sugarcat.domain.meal.repository.MealRecordRepository;
import com.seoltangmyo.sugarcat.domain.user.dto.MessageResponse;
import com.seoltangmyo.sugarcat.domain.user.entity.User;
import com.seoltangmyo.sugarcat.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MealRecordService {

    private final MealRecordRepository mealRecordRepository;
    private final UserRepository userRepository;

    // 식사 기록 저장
    // POST /api/v1/meals/me?date={date}&sequence={sequence}
    // 같은 날짜+순번 기록이 이미 있으면 예외 처리
    @Transactional
    public MessageResponse createMealRecord(UUID userId, LocalDate date, int sequence, MealRecordCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cat cat = user.getCat();
        if (cat == null) {
            throw new IllegalArgumentException("등록된 고양이가 없습니다.");
        }

        boolean exists = mealRecordRepository.existsByCatAndRecordDateAndSequence(cat, date, sequence);
        if (exists) {
            throw new IllegalArgumentException("이미 해당 순번의 식사 기록이 존재합니다.");
        }

        MealStatus mealStatus = MealStatus.valueOf(request.mealStatus());
        MealRecord mealRecord = MealRecord.create(cat, user, date, request.recordTime(), sequence, mealStatus);
        mealRecordRepository.save(mealRecord);

        return new MessageResponse("식사 기록이 저장되었습니다.");
    }

    // 날짜별 식사 기록 조회
    // GET /api/v1/meals/me?date={date}
    @Transactional
    public MealRecordListResponse getMealRecords(UUID userId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cat cat = user.getCat();
        if (cat == null) {
            throw new IllegalArgumentException("등록된 고양이가 없습니다.");
        }

        List<MealRecord> records = mealRecordRepository.findAllByCatAndRecordDateOrderBySequenceAsc(cat, date);

        List<MealRecordResponse> responses = records.stream()
                .map(r -> new MealRecordResponse(
                        r.getSequence(),
                        r.getRecordTime(),
                        r.getMealStatus().name(),
                        r.getRecordedBy() != null ? r.getRecordedBy().getNickname() : "탈퇴한 사용자"
                ))
                .toList();

        return new MealRecordListResponse(responses);
    }
}
