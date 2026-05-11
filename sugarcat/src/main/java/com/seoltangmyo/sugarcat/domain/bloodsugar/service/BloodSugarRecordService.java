package com.seoltangmyo.sugarcat.domain.bloodsugar.service;

import com.seoltangmyo.sugarcat.domain.bloodsugar.dto.BloodSugarRecordCreateRequest;
import com.seoltangmyo.sugarcat.domain.bloodsugar.dto.BloodSugarRecordCreateResponse;

import java.util.UUID;

public interface BloodSugarRecordService {
    BloodSugarRecordCreateResponse createBloodSugarRecord(UUID userId, BloodSugarRecordCreateRequest request);
}
