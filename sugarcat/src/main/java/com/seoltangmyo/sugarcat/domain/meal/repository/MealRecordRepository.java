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

    // 날짜별 식사 기록 조회 (순번 오름차순)
    List<MealRecord> findAllByCatAndRecordDateOrderBySequenceAsc(Cat cat, LocalDate recordDate);

    // 날짜 + 순번으로 단건 조회 (식사 기록 수정 시 사용)
    // [추가: meaningGitt] PATCH /api/v1/meals/me 구현을 위해 추가
    java.util.Optional<MealRecord> findByCatAndRecordDateAndSequence(Cat cat, LocalDate recordDate, int sequence);

    // 고양이에 속한 모든 식사 기록 삭제 (고양이 hard delete 시 연관 데이터 정리용)
    // [추가: meaningGitt] DELETE /api/v1/cats/me 구현을 위해 추가
    void deleteAllByCat(Cat cat);
}
