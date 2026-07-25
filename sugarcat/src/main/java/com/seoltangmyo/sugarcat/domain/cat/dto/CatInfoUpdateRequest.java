package com.seoltangmyo.sugarcat.domain.cat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

// PATCH /api/v1/cats/me 요청 바디
// 고양이 기본 정보 수정 (이름, 생년월일, 진단일)
public record CatInfoUpdateRequest(
        @NotBlank(message = "고양이 이름은 필수입니다.")
        @Size(max = 10, message = "고양이 이름은 최대 10자까지 입력할 수 있습니다.")
        String name,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthDate,       // null 허용
        @NotNull(message = "당뇨 진단일은 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate diagnosedDate
) {
}
