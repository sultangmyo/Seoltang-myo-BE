package com.seoltangmyo.sugarcat.domain.bloodsugar.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seoltangmyo.sugarcat.domain.bloodsugar.entity.SugarStatus;

import java.time.LocalTime;

public record BloodSugarRecordResponse(
        String nickname,
        @JsonFormat(pattern = "HH:mm") LocalTime recordTime,
        int sequence,
        int sugarValue,
        SugarStatus sugarStatus
) {
}
