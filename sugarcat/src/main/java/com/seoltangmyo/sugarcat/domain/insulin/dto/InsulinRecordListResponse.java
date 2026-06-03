package com.seoltangmyo.sugarcat.domain.insulin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

// GET /api/v1/insulin-records/me?date={date} 응답
// 날짜별 인슐린 투여 기록 목록 반환
public record InsulinRecordListResponse(
        List<InsulinRecordItem> records
) {
    // 개별 기록 항목
    // @JsonProperty("isInjected"): boolean 필드에 is-prefix가 있으면 Jackson이 "is"를 제거해
    // "injected"로 직렬화하므로, iOS 협의 키("isInjected")와 일치시키기 위해 명시적으로 지정
    // nickname: 기록한 집사 닉네임 (탈퇴 시 "탈퇴한 사용자")
    public record InsulinRecordItem(
            int sequence,
            @JsonProperty("isInjected") boolean isInjected,
            String nickname
    ) {}
}
