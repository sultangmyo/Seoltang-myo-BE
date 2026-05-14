package com.seoltangmyo.sugarcat.domain.bloodsugar.entity;

import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "blood_sugar_records")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BloodSugarRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID bloodSugarRecordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id",nullable = false)
    private Cat cat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by_user_id")
    private User recordedBy;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "record_time", nullable = false)
    private LocalTime recordTime;

    @Column(name = "sequence", nullable = false)
    private int sequence;

    @Column(name = "sugar_value", nullable = false)
    private int sugarValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "sugar_status", length = 20, nullable = false)
    private SugarStatus sugarStatus;


    public void update(
            LocalTime recordTime,
            int sugarValue,
            SugarStatus sugarStatus
    ) {
        this.recordTime = recordTime;
        this.sugarValue = sugarValue;
        this.sugarStatus = sugarStatus;
    }

}
