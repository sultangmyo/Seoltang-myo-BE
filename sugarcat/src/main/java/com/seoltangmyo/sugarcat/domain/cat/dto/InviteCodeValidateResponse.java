package com.seoltangmyo.sugarcat.domain.cat.dto;

import java.util.UUID;

// GET /api/v1/cats/invite?code={inviteCode} 응답
// 초대코드 유효성 검증 성공 시 고양이 ID와 이름 반환
// 프론트에서 "'나비' 고양이 그룹에 들어가시겠습니까?" 팝업 표시에 사용
public record InviteCodeValidateResponse(
        UUID catId,
        String catName
) {
}
