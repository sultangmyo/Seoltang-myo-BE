package com.seoltangmyo.sugarcat.domain.schedule.entity;

import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(
        name = "care_schedules",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_care_schedules_cat_type_sequence",
                        columnNames = {"cat_id", "schedule_type", "sequence"}
                )
        }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CareSchedule extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id", nullable = false)
    private Cat cat;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", length = 20, nullable = false)
    private CareScheduleType scheduleType;

    @Column(name = "sequence", nullable = false)
    private int sequence;

    @Column(name = "scheduled_time", nullable = false)
    private LocalTime scheduledTime;

    // 루틴 스케줄 생성 팩토리 메서드
    // sequence는 프론트에서 전달한 값 그대로 저장 (1회차, 2회차 …)
    public static CareSchedule create(
            Cat cat,
            CareScheduleType scheduleType,
            int sequence,
            LocalTime scheduledTime
    ) {
        CareSchedule schedule = new CareSchedule();
        schedule.cat = cat;
        schedule.scheduleType = scheduleType;
        schedule.sequence = sequence;
        schedule.scheduledTime = scheduledTime;
        return schedule;
    }

}
