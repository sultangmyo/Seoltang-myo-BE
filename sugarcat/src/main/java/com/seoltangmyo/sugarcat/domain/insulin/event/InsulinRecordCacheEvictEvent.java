package com.seoltangmyo.sugarcat.domain.insulin.event;

import java.time.LocalDate;
import java.util.UUID;

public record InsulinRecordCacheEvictEvent (
        UUID catId,
        LocalDate recordDate
){
}
