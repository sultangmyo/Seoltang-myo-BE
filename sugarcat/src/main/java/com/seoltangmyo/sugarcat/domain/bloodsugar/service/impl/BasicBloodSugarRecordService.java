package com.seoltangmyo.sugarcat.domain.bloodsugar.service.impl;

import com.seoltangmyo.sugarcat.domain.bloodsugar.dto.BloodSugarRecordCreateRequest;
import com.seoltangmyo.sugarcat.domain.bloodsugar.dto.BloodSugarRecordCreateResponse;
import com.seoltangmyo.sugarcat.domain.bloodsugar.repository.BloodSugarRecordRepository;
import com.seoltangmyo.sugarcat.domain.bloodsugar.service.BloodSugarRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBloodSugarRecordService implements BloodSugarRecordService {
    private final BloodSugarRecordRepository bloodSugarRecordRepository;

    @Override
    public BloodSugarRecordCreateResponse createBloodSugarRecord(UUID userId, BloodSugarRecordCreateRequest request) {
        return null;
    }

}
