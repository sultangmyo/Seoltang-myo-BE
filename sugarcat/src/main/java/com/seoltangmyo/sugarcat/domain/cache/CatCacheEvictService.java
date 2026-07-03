package com.seoltangmyo.sugarcat.domain.cache;

import com.seoltangmyo.sugarcat.domain.schedule.entity.CareScheduleType;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CatCacheEvictService {

    @CacheEvict(
            cacheNames = "catInfo",
            key = "#catId"
    )
    public void evictCatInfo(UUID catId) {}

    @CacheEvict(
            cacheNames = "careSchedules",
            key = "{#catId, #type}"
    )
    public void evictCareSchedules(
            UUID catId,
            CareScheduleType type
    ) {}

    @CacheEvict(
            cacheNames = "dailyInsulinRecords",
            key = "{#catId, #date}"
    )
    public void evictDailyInsulinRecords(
            UUID catId,
            LocalDate date
    ) {}

}
