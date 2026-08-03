package com.seoltangmyo.sugarcat.domain.cat.repository;

import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CatRepository extends JpaRepository<Cat, UUID> {
    Optional<Cat> findByInviteCode(String inviteCode);
}
