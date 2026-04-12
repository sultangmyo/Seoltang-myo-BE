package com.seoltangmyo.sugarcat.domain.schedule.entity;

import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "care_schedules")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CareSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID careScheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id", nullable = false)
    private Cat cat;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", length = 20, nullable = false)
    private CareScheduleType scheduleType;

    @Column(name = "sequence", nullable = false)
    private int sequence;

    @Column(name = "scheduled_time", nullable = false)
    private LocalTime scheduleTime;

}
