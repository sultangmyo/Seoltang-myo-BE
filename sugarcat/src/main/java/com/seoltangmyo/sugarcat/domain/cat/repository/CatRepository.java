package com.seoltangmyo.sugarcat.domain.cat.repository;

import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CatRepository extends JpaRepository<Cat, UUID> {

    // 초대코드로 고양이 조회 (공동 집사 합류 시 사용)
    // 초대코드가 없거나 유효하지 않으면 Optional.empty() 반환
    Optional<Cat> findByInviteCode(String inviteCode);
}
