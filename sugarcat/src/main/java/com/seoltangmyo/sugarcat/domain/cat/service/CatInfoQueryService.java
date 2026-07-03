package com.seoltangmyo.sugarcat.domain.cat.service;

import com.seoltangmyo.sugarcat.domain.cat.dto.CatInfoResponse;
import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.cat.repository.CatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatInfoQueryService {

    private final CatRepository catRepository;

    @Cacheable(
            cacheNames = "catInfo",
            key = "#catId"
    )
    @Transactional(readOnly = true) // 조회 전용 트랜잭션
    public CatInfoResponse getCatInfo(UUID catId) {
        log.info("[고양이 정보 조회 - 캐시 미스] catId={}", catId);

        Cat cat = catRepository.findById(catId)
                .orElseThrow(() -> new IllegalArgumentException("고양이를 찾을 수 없습니다."));

        return new CatInfoResponse(
                cat.getName(),
                cat.getBirthDate(),
                cat.getDiagnosedDate()
        );
    }

}