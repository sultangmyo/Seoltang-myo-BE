package com.seoltangmyo.sugarcat.domain.bloodsugar.service.impl;

import com.seoltangmyo.sugarcat.domain.bloodsugar.dto.BloodSugarRecordCreateRequest;
import com.seoltangmyo.sugarcat.domain.bloodsugar.dto.BloodSugarRecordCreateResponse;
import com.seoltangmyo.sugarcat.domain.bloodsugar.entity.BloodSugarRecord;
import com.seoltangmyo.sugarcat.domain.bloodsugar.entity.SugarStatus;
import com.seoltangmyo.sugarcat.domain.bloodsugar.repository.BloodSugarRecordRepository;
import com.seoltangmyo.sugarcat.domain.bloodsugar.service.BloodSugarRecordService;
import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.user.entity.User;
import com.seoltangmyo.sugarcat.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBloodSugarRecordService implements BloodSugarRecordService {
    private final BloodSugarRecordRepository bloodSugarRecordRepository;
    private final UserRepository userRepository;

    @Override
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

}
