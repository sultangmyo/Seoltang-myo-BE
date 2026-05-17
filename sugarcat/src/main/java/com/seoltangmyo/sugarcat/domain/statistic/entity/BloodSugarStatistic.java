package com.seoltangmyo.sugarcat.domain.statistic.entity;

import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "blood_sugar_statistics")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BloodSugarStatistic extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id", nullable = false)
    private Cat cat;

    @Enumerated(EnumType.STRING)
    @Column(name = "period_type", length = 20, nullable = false)
    private PeriodType periodType;

    @Column(name = "target_date",nullable = false)
    private LocalDate targetDate;

    @Column(name = "avg_sugar")
    private Integer avgSugar;

    @Column(name = "max_sugar")
    private Integer maxSugar;

    @Column(name = "record_count", nullable = false)
    private int recordCount;

}
