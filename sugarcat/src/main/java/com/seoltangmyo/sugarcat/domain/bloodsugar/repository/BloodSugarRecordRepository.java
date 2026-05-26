package com.seoltangmyo.sugarcat.domain.bloodsugar.repository;

import com.seoltangmyo.sugarcat.domain.bloodsugar.entity.BloodSugarRecord;
import com.seoltangmyo.sugarcat.domain.bloodsugar.entity.SugarStatus;
import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BloodSugarRecordRepository extends JpaRepository<BloodSugarRecord, UUID> {

    List<BloodSugarRecord> findAllByCatAndRecordDateOrderBySequenceAsc(
            Cat cat,
            LocalDate recordDate
    );

    Optional<BloodSugarRecord> findByCatAndRecordDateAndSequence(
            Cat cat,
            LocalDate localDate,
            int sequence
    );

    // 해당 날짜 + 순번 기록이 있는지 확인
    boolean existsByCatAndRecordDateAndSequence(Cat cat, LocalDate recordDate, int sequence);

    // 기간 내 전체 혈당 기록 개수
    long countByCatAndRecordDateBetween(
            Cat cat,
            LocalDate startDate,
            LocalDate endDate
    );

    // 기간 내 상태 별 혈당 기록 개수
    long countByCatAndRecordDateBetweenAndSugarStatus(
            Cat cat,
            LocalDate startDate,
            LocalDate endDate,
            SugarStatus sugarStatus
    );

    List<BloodSugarRecord> findAllByCatAndRecordDateBetween(
            Cat cat,
            LocalDate startDate,
            LocalDate endDate
    );
}