package com.seoltangmyo.sugarcat.domain.insulin.service;

import com.seoltangmyo.sugarcat.domain.cache.CatCacheEvictService;
import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.insulin.dto.InsulinRecordCreateRequest;
import com.seoltangmyo.sugarcat.domain.insulin.dto.InsulinRecordListResponse;
import com.seoltangmyo.sugarcat.domain.insulin.entity.InsulinRecord;
import com.seoltangmyo.sugarcat.domain.insulin.event.InsulinRecordCacheEvictEvent;
import com.seoltangmyo.sugarcat.domain.insulin.event.InsulinRecordCreatedEvent;
import com.seoltangmyo.sugarcat.domain.insulin.repository.InsulinRecordRepository;
import com.seoltangmyo.sugarcat.domain.user.dto.MessageResponse;
import com.seoltangmyo.sugarcat.domain.user.entity.User;
import com.seoltangmyo.sugarcat.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsulinRecordService {

    private final InsulinRecordRepository insulinRecordRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final InsulinRecordQueryService insulinRecordQueryService;
    private final CatCacheEvictService catCacheEvictService;

    // 인슐린 투여 기록 저장
    // POST /api/v1/insulin-records/me
    // 같은 날짜+순번 기록이 이미 있으면 예외 처리
    @Transactional
    public MessageResponse createInsulinRecord(UUID userId, InsulinRecordCreateRequest request) {
        log.info("[인슐린 기록 저장] userId={}, date={}, sequence={}, isInjected={}",
                userId, request.recordDate(), request.sequence(), request.isInjected());

        if(!request.isInjected()) {
            log.warn("[인슐린 기록 저장 실패] isInjected=false 요청 - userId={}, date={}, sequence={}",
                    userId, request.recordDate(), request.sequence());
            throw new IllegalArgumentException("인슐린 투여 기록은 투여한 경우에만 저장할 수 있습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cat cat = user.getCat();

        if (cat == null) {
            log.warn("[인슐린 기록 저장 실패] 고양이 없음 - userId={}", userId);
            throw new IllegalArgumentException("등록된 고양이가 없습니다.");
        }

        boolean exists = insulinRecordRepository.existsByCatAndRecordDateAndSequence(
                cat, request.recordDate(), request.sequence()
        );

        if (exists) {
            log.warn("[인슐린 기록 저장 실패] 중복 기록 - catId={}, date={}, sequence={}",
                    cat.getId(), request.recordDate(), request.sequence());
            throw new IllegalArgumentException("이미 해당 순번의 인슐린 기록이 존재합니다.");
        }

        InsulinRecord record = InsulinRecord.create(
                cat,
                user,
                request.recordDate(),
                request.sequence(),
                true
        );

        insulinRecordRepository.save(record);

        eventPublisher.publishEvent(
                new InsulinRecordCacheEvictEvent(
                        cat.getId(),
                        request.recordDate()
                )
        );

        log.info("[인슐린 기록 저장 완료] recordId={}", record.getId());

        eventPublisher.publishEvent(new InsulinRecordCreatedEvent(record.getId()));
        log.info("[인슐린 기록 저장 이벤트 발행] recordId={}", record.getId());

        return new MessageResponse("인슐린 투여 기록이 저장되었습니다.");
    }

    // 날짜별 인슐린 투여 기록 조회
    // GET /api/v1/insulin-records/me?date={date}
    // nickName: 기록한 집사 닉네임, 탈퇴 시 "탈퇴한 사용자" 반환
    @Transactional(readOnly = true)
    public InsulinRecordListResponse getInsulinRecords(UUID userId, LocalDate date) {
        log.info("[인슐린 기록 조회] userId={}, date={}", userId, date);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cat cat = user.getCat();

        return insulinRecordQueryService.getInsulinRecords(
                cat.getId(),
                date
        );
    }
}
