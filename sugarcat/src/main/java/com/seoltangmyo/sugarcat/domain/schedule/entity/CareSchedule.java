package com.seoltangmyo.sugarcat.domain.schedule.entity;

import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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

}
