package com.seoltangmyo.sugarcat.domain.schedule.repository;

import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.schedule.entity.CareSchedule;
import com.seoltangmyo.sugarcat.domain.schedule.entity.CareScheduleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface CareScheduleRepository extends JpaRepository<CareSchedule, UUID> {

    // 현재 시간과 일치하는 스케줄 조회
    List<CareSchedule> findAllByScheduledTime(LocalTime scheduledTime);

    // 고양이 + 타입별 스케줄 조회 (순번 오름차순)
    List<CareSchedule> findAllByCatAndScheduleTypeOrderBySequenceAsc(Cat cat, CareScheduleType scheduleType);

    // 고양이 + 타입별 스케줄 전체 삭제 (수정 시 기존 삭제 후 재저장)
    void deleteAllByCatAndScheduleType(Cat cat, CareScheduleType scheduleType);
}
