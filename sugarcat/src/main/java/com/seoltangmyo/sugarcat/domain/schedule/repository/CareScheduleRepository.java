package com.seoltangmyo.sugarcat.domain.schedule.repository;

import com.seoltangmyo.sugarcat.domain.schedule.entity.CareSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CareScheduleRepository extends JpaRepository<CareSchedule, UUID> {
    // 현재는 기본 save / findById 만 사용
    // 루틴 수정 기능 추가 시 catId + scheduleType 기반 조회 메서드 추가 예정
}
