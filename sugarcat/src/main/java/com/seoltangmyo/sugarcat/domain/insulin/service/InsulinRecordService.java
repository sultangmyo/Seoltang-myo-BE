package com.seoltangmyo.sugarcat.domain.insulin.service;

import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.insulin.dto.InsulinRecordCreateRequest;
import com.seoltangmyo.sugarcat.domain.insulin.entity.InsulinRecord;
import com.seoltangmyo.sugarcat.domain.insulin.repository.InsulinRecordRepository;
import com.seoltangmyo.sugarcat.domain.user.dto.MessageResponse;
import com.seoltangmyo.sugarcat.domain.user.entity.User;
import com.seoltangmyo.sugarcat.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InsulinRecordService {

    private final InsulinRecordRepository insulinRecordRepository;
    private final UserRepository userRepository;

    // 인슐린 투여 기록 저장
    // POST /api/v1/insulin-records/me
    // 같은 날짜+순번 기록이 이미 있으면 예외 처리
    @Transactional
    public MessageResponse createInsulinRecord(UUID userId, InsulinRecordCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cat cat = user.getCat();
        if (cat == null) {
            throw new IllegalArgumentException("등록된 고양이가 없습니다.");
        }

        boolean exists = insulinRecordRepository.existsByCatAndRecordDateAndSequence(
                cat, request.recordDate(), request.sequence()
        );
        if (exists) {
            throw new IllegalArgumentException("이미 해당 순번의 인슐린 기록이 존재합니다.");
        }

        InsulinRecord record = InsulinRecord.create(cat, user, request.recordDate(), request.sequence(), request.isInjected());
        insulinRecordRepository.save(record);

        return new MessageResponse("인슐린 투여 기록이 저장되었습니다.");
    }
}
