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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cat cat = user.getCat();

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

        return new BloodSugarRecordCreateResponse(
                bloodSugarRecord.getBloodSugarRecordId(),
                bloodSugarRecord.getSugarStatus()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public BloodSugarRecordListResponse getBloodSugarRecordsByDate(
            UUID userId,
            LocalDate date
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cat cat = user.getCat();

        List<BloodSugarRecord> records =
                bloodSugarRecordRepository.findAllByCatAndRecordDateOrderBySequenceAsc(cat, date);

        List<BloodSugarRecordResponse> recordResponses = records.stream()
                .map(record -> new BloodSugarRecordResponse(
                        user.getNickname(),
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
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cat cat = user.getCat();

        BloodSugarRecord bloodSugarRecord =
                bloodSugarRecordRepository.findByCatAndRecordDateAndSequence(
                        cat,
                        request.recordedDate(),
                        request.sequence()
                ).orElseThrow(()-> new IllegalArgumentException("혈당 기록을 찾을 수 없습니다."));

        SugarStatus sugarStatus = SugarStatus.from(request.sugarValue());

        // 더티체킹
        bloodSugarRecord.update(
                request.recordedTime(),
                request.sugarValue(),
                sugarStatus
        );
    }

    @Override
    @Transactional
    public void deleteBloodSugarRecord(
            UUID userId,
            LocalDate date,
            int sequence
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cat cat = user.getCat();

        BloodSugarRecord bloodSugarRecord =
                bloodSugarRecordRepository.findByCatAndRecordDateAndSequence(
                        cat,
                        date,
                        sequence
                ).orElseThrow(()-> new IllegalArgumentException("혈당 기록을 찾을 수 없습니다."));

        bloodSugarRecordRepository.delete(bloodSugarRecord);
    }

}
