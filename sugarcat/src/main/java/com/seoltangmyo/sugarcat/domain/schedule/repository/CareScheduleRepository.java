package com.seoltangmyo.sugarcat.domain.schedule.repository;

import com.seoltangmyo.sugarcat.domain.schedule.entity.CareSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface CareScheduleRepository extends JpaRepository<CareSchedule, UUID> {

    // 현재 시간과 일치하는 스케줄 조회
    List<CareSchedule> findAllByScheduledTime(LocalTime scheduledTime);
}
