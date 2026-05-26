package com.seoltangmyo.sugarcat.global.security;

import com.seoltangmyo.sugarcat.domain.user.entity.User;
import com.seoltangmyo.sugarcat.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userIdText) {
        UUID userId = UUID.fromString(userIdText);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("유저 없음"));

        return new CustomUserDetails(user.getId(), user.getNickname());
    }
}
