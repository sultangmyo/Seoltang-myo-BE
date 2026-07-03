package com.seoltangmyo.sugarcat.domain.insulin.listener;

import com.seoltangmyo.sugarcat.domain.cache.CatCacheEvictService;
import com.seoltangmyo.sugarcat.domain.insulin.event.InsulinRecordCacheEvictEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class InsulinRecordCacheEventListener {

    private final CatCacheEvictService catCacheEvictService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleInsulinRecordCacheEvict(InsulinRecordCacheEvictEvent event) {
        catCacheEvictService.evictDailyInsulinRecords(
                event.catId(),
                event.recordDate()
        );
    }
}
