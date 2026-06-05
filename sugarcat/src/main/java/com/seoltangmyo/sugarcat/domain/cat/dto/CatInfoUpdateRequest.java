package com.seoltangmyo.sugarcat.domain.cat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

// PATCH /api/v1/cats/me 요청 바디
// 고양이 기본 정보 수정 (이름, 생년월일, 진단일)
public record CatInfoUpdateRequest(
        String name,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthDate,       // null 허용
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate diagnosedDate
) {
}
