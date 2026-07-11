package com.seoltangmyo.sugarcat.domain.meal;

import com.seoltangmyo.sugarcat.domain.cache.CatCacheEvictService;
import com.seoltangmyo.sugarcat.domain.meal.event.MealRecordCacheEvictEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MealRecordCacheEventListener {

    private final CatCacheEvictService catCacheEvictService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMealRecordCacheEvict(MealRecordCacheEvictEvent event) {
        catCacheEvictService.evictDailyMealRecords(
                event.catId(),
                event.recordDate()
        );
    }
}
