package com.seoltangmyo.sugarcat.domain.bloodsugar.dto;

import com.seoltangmyo.sugarcat.domain.bloodsugar.entity.SugarStatus;

import java.util.UUID;

public record BloodSugarRecordCreateResponse(
        UUID id,
        SugarStatus sugarStatus
) {
}
