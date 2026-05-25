package com.seoltangmyo.sugarcat.domain.bloodsugar.dto;

import java.util.List;

public record BloodSugarRecordListResponse(
        List<BloodSugarRecordResponse> records
) {
}
