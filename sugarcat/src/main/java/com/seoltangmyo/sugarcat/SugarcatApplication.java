package com.seoltangmyo.sugarcat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // @Scheduled 기능 활성화
@EnableJpaAuditing // 시간 자동 입력 설정
@SpringBootApplication
public class SugarcatApplication {

    public static void main(String[] args) {
        SpringApplication.run(SugarcatApplication.class, args);
    }

}
