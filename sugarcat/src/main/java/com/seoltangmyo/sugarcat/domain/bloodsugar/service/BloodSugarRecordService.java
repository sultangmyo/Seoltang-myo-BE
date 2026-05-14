package com.seoltangmyo.sugarcat.domain.bloodsugar.service;

import com.seoltangmyo.sugarcat.domain.bloodsugar.dto.BloodSugarRecordCreateRequest;
import com.seoltangmyo.sugarcat.domain.bloodsugar.dto.BloodSugarRecordCreateResponse;
import com.seoltangmyo.sugarcat.domain.bloodsugar.dto.BloodSugarRecordListResponse;

import java.time.LocalDate;
import java.util.UUID;

public interface BloodSugarRecordService {
    BloodSugarRecordCreateResponse createBloodSugarRecord(UUID userId, BloodSugarRecordCreateRequest request);

    BloodSugarRecordListResponse getBloodSugarRecordsByDate(UUID userId, LocalDate date);
}
