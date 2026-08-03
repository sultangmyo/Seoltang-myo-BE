package com.seoltangmyo.sugarcat.domain.bloodsugar.listener;

import com.seoltangmyo.sugarcat.domain.bloodsugar.event.BloodSugarRecordCacheEvictEvent;
import com.seoltangmyo.sugarcat.domain.cache.CatCacheEvictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BloodSugarRecordCacheEventListener {

    private final CatCacheEvictService catCacheEvictService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCacheEvict(BloodSugarRecordCacheEvictEvent event) {
        catCacheEvictService.evictDailyBloodSugarRecords(
                event.catId(),
                event.recordDate()
        );
    }
}
