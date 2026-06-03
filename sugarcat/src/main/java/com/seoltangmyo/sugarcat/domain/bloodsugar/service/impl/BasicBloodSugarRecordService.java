package com.seoltangmyo.sugarcat.domain.bloodsugar.service.impl;

import com.seoltangmyo.sugarcat.domain.bloodsugar.dto.*;
import com.seoltangmyo.sugarcat.domain.bloodsugar.entity.BloodSugarRecord;
import com.seoltangmyo.sugarcat.domain.bloodsugar.entity.SugarStatus;
import com.seoltangmyo.sugarcat.domain.bloodsugar.repository.BloodSugarRecordRepository;
import com.seoltangmyo.sugarcat.domain.bloodsugar.service.BloodSugarRecordService;
import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.user.entity.User;
import com.seoltangmyo.sugarcat.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicBloodSugarRecordService implements BloodSugarRecordService {
    private final BloodSugarRecordRepository bloodSugarRecordRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BloodSugarRecordCreateResponse createBloodSugarRecord(
            UUID userId,
            BloodSugarRecordCreateRequest request
    ) {
        log.info("[혈당 기록 저장] userId={}, date={}, sequence={}, sugarValue={}",
                userId, request.recordedDate(), request.sequence(), request.sugarValue());

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cat cat = user.getCat();

        // 같은 날짜 + 순번 기록 중복 저장 방지
        if (bloodSugarRecordRepository.existsByCatAndRecordDateAndSequence(
                cat, request.recordedDate(), request.sequence())) {
            log.warn("[혈당 기록 저장 실패] 중복 기록 - catId={}, date={}, sequence={}",
                    cat.getId(), request.recordedDate(), request.sequence());
            throw new IllegalArgumentException("이미 해당 순번의 혈당 기록이 존재합니다.");
        }

        SugarStatus sugarStatus = SugarStatus.from(request.sugarValue());

        BloodSugarRecord bloodSugarRecord = BloodSugarRecord.builder()
                .cat(cat)
                .recordedBy(user)
                .recordDate(request.recordedDate())
                .recordTime(request.recordedTime())
                .sequence(request.sequence())
                .sugarValue(request.sugarValue())
                .sugarStatus(sugarStatus)
                .build();

        bloodSugarRecordRepository.save(bloodSugarRecord);

        log.info("[혈당 기록 저장 완료] recordId={}, sugarStatus={}", bloodSugarRecord.getId(), sugarStatus);

        return new BloodSugarRecordCreateResponse(
                bloodSugarRecord.getId(),
                bloodSugarRecord.getSugarStatus()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public BloodSugarRecordListResponse getBloodSugarRecordsByDate(
            UUID userId,
            LocalDate date
    ) {
        log.info("[혈당 기록 조회] userId={}, date={}", userId, date);

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cat cat = user.getCat();

        List<BloodSugarRecord> records =
                bloodSugarRecordRepository.findAllByCatAndRecordDateOrderBySequenceAsc(cat, date);

        log.info("[혈당 기록 조회 완료] catId={}, date={}, count={}", cat.getId(), date, records.size());

        List<BloodSugarRecordResponse> recordResponses = records.stream()
                .map(record -> new BloodSugarRecordResponse(
                        record.getRecordedBy() != null ? record.getRecordedBy().getNickname() : "탈퇴한 사용자",
                        record.getRecordTime(),
                        record.getSequence(),
                        record.getSugarValue(),
                        record.getSugarStatus()
                ))
                .toList();

        return new BloodSugarRecordListResponse(recordResponses);
    }

    @Override
    @Transactional
    public void updateBloodSugarRecord(
            UUID userId,
            BloodSugarRecordUpdateRequest request
    ) {
        log.info("[혈당 기록 수정] userId={}, date={}, sequence={}", userId, request.recordedDate(), request.sequence());

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cat cat = user.getCat();

        BloodSugarRecord bloodSugarRecord =
                bloodSugarRecordRepository.findByCatAndRecordDateAndSequence(
                        cat,
                        request.recordedDate(),
                        request.sequence()
                ).orElseThrow(() -> {
                    log.warn("[혈당 기록 수정 실패] 기록 없음 - catId={}, date={}, sequence={}",
                            cat.getId(), request.recordedDate(), request.sequence());
                    return new IllegalArgumentException("혈당 기록을 찾을 수 없습니다.");
                });

        SugarStatus sugarStatus = SugarStatus.from(request.sugarValue());

        // 더티체킹
        bloodSugarRecord.update(
                request.recordedTime(),
                request.sugarValue(),
                sugarStatus
        );

        log.info("[혈당 기록 수정 완료] recordId={}", bloodSugarRecord.getId());
    }

    @Override
    @Transactional
    public void deleteBloodSugarRecord(
            UUID userId,
            LocalDate date,
            int sequence
    ) {
        log.info("[혈당 기록 삭제] userId={}, date={}, sequence={}", userId, date, sequence);

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cat cat = user.getCat();

        BloodSugarRecord bloodSugarRecord =
                bloodSugarRecordRepository.findByCatAndRecordDateAndSequence(
                        cat,
                        date,
                        sequence
                ).orElseThrow(() -> {
                    log.warn("[혈당 기록 삭제 실패] 기록 없음 - catId={}, date={}, sequence={}", cat.getId(), date, sequence);
                    return new IllegalArgumentException("혈당 기록을 찾을 수 없습니다.");
                });

        bloodSugarRecordRepository.delete(bloodSugarRecord);

        log.info("[혈당 기록 삭제 완료] recordId={}", bloodSugarRecord.getId());
    }

}
