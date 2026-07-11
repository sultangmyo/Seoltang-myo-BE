package com.seoltangmyo.sugarcat.domain.cache;

import com.seoltangmyo.sugarcat.domain.schedule.entity.CareScheduleType;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@SuppressWarnings("unused")
public class CatCacheEvictService {

    // 고양이 정보 수정
    @CacheEvict(
            cacheNames = "catInfo",
            key = "#catId"
    )
    public void evictCatInfo(UUID catId) {}

    // 스케줄 수정
    @CacheEvict(
            cacheNames = "careSchedules",
            key = "{#catId, #type}"
    )
    public void evictCareSchedules(
            UUID catId,
            CareScheduleType type
    ) {}

    // 기록 저장,수정,삭제
    @CacheEvict(
            cacheNames = "dailyInsulinRecords",
            key = "{#catId, #date}"
    )
    public void evictDailyInsulinRecords(
            UUID catId,
            LocalDate date
    ) {}

    // 닉네임 수정, 사용자 탈퇴
    @CacheEvict(
            cacheNames = "dailyBloodSugarRecords",
            key = "{#catId, #date}"
    )
    public void evictDailyBloodSugarRecords(
            UUID catId,
            LocalDate date
    ) {}

    @CacheEvict(
            cacheNames = "dailyMealRecords",
            key = "{#catId, #date}"
    )
    public void evictDailyMealRecords(
            UUID catId,
            LocalDate date
    ) {}

    // 닉네임 수정, 사용자 탈퇴
    @Caching(evict = {
            @CacheEvict(
                    cacheNames = "dailyMealRecords",
                    allEntries = true
            ),
            @CacheEvict(
                    cacheNames = "dailyBloodSugarRecords",
                    allEntries = true
            ),
            @CacheEvict(
                    cacheNames = "dailyInsulinRecords",
                    allEntries = true
            )
    })
    public void evictAllRecordCaches() {}

    // 마지막 사용자 탈퇴 (= 고양이 삭제)
//    @Caching(evict = {
//            @CacheEvict(
//                    cacheNames = "catInfo",
//                    key = "#catId"
//            ),
//            @CacheEvict(
//                    cacheNames = "careSchedules",
//                    allEntries = true
//            ),
//            @CacheEvict(
//                    cacheNames = "dailyMealRecords",
//                    allEntries = true
//            ),
//            @CacheEvict(
//                    cacheNames = "dailyBloodSugarRecords",
//                    allEntries = true
//            ),
//            @CacheEvict(
//                    cacheNames = "dailyInsulinRecords",
//                    allEntries = true
//            )
//    })
//    public void evictAllCatRelatedCaches(UUID catId) {}
}
