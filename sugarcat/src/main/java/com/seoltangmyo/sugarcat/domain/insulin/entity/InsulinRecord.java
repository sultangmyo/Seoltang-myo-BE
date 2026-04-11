package com.seoltangmyo.sugarcat.domain.insulin.entity;

import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "insulin_records")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class InsulinRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID insulinRecordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id",nullable = false)
    private Cat cat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by_user_id")
    private User recordedBy;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "sequence", nullable = false)
    private int sequence;

    @Column(name = "is_injected", nullable = false)
    private Boolean isInjected = false;


}
