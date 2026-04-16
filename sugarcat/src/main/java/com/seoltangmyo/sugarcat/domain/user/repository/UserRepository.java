package com.seoltangmyo.sugarcat.domain.user.repository;

import com.seoltangmyo.sugarcat.domain.user.entity.ProviderType;
import com.seoltangmyo.sugarcat.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    // 계정이 이미 있는지 확인
    Optional<User> findByProviderAndProviderId(ProviderType provider, String providerId);
}
