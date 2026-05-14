package com.seoltangmyo.sugarcat.domain.bloodsugar.repository;

import com.seoltangmyo.sugarcat.domain.bloodsugar.entity.BloodSugarRecord;
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
            int sequence);
}
