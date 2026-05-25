package com.seoltangmyo.sugarcat.domain.bloodsugar.dto;

import com.seoltangmyo.sugarcat.domain.bloodsugar.entity.SugarStatus;

import java.time.LocalTime;

public record BloodSugarRecordResponse(
        String nickName,
        LocalTime recordTime,
        int sequence,
        int sugarValue,
        SugarStatus sugarStatus
) {
}
