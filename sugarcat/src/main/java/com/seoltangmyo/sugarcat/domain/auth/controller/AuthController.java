package com.seoltangmyo.sugarcat.domain.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

//    private final AuthService authService; 서비스 추가 예정
//    private final JwtProvider jwtProvider; 추가 예정

    @PostMapping("/apple")
    public ResponseEntity<Void> appleLogin() {
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/kakao")
    public ResponseEntity<Void> kakaoLogin() {
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> createRefreshToken() {
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }
}