package com.seoltangmyo.sugarcat.domain.cat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

// GET /api/v1/cats/me 응답
// 홈뷰 헤더에 사용되는 고양이 기본 정보 반환
public record CatInfoResponse(
        String name,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthDate,       // null 허용 (미입력 시 null)
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate diagnosedDate
) {
}
