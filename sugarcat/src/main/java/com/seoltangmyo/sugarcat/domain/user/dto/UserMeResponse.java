package com.seoltangmyo.sugarcat.domain.user.dto;

import java.util.List;

// GET /api/v1/users/me 응답
// 내 닉네임 + 같은 cat_id를 가진 다른 집사 닉네임 목록 반환
public record UserMeResponse(
        String nickname,
        List<FamilyMember> family
) {
    public record FamilyMember(String nickname) {}
}
