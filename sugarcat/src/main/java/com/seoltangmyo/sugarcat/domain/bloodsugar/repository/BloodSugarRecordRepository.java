package com.seoltangmyo.sugarcat.domain.bloodsugar.repository;

import com.seoltangmyo.sugarcat.domain.bloodsugar.entity.BloodSugarRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BloodSugarRecordRepository extends JpaRepository<BloodSugarRecord, UUID> {
}
