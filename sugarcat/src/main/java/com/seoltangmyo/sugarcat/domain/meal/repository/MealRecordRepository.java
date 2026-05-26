package com.seoltangmyo.sugarcat.domain.meal.repository;

import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.meal.entity.MealRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface MealRecordRepository extends JpaRepository<MealRecord, UUID> {

    // 해당 날짜 + 순번 기록이 있는지 확인
    boolean existsByCatAndRecordDateAndSequence(Cat cat, LocalDate recordDate, int sequence);

    List<MealRecord> findAllByCatAndRecordDateBetween(
            Cat cat,
            LocalDate startDate,
            LocalDate endDate
    );
}
