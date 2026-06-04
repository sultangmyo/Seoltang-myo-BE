package com.seoltangmyo.sugarcat.domain.notification.listener;

import com.seoltangmyo.sugarcat.domain.bloodsugar.entity.BloodSugarRecord;
import com.seoltangmyo.sugarcat.domain.bloodsugar.event.BloodSugarRecordCreatedEvent;
import com.seoltangmyo.sugarcat.domain.bloodsugar.repository.BloodSugarRecordRepository;
import com.seoltangmyo.sugarcat.domain.insulin.entity.InsulinRecord;
import com.seoltangmyo.sugarcat.domain.insulin.event.InsulinRecordCreatedEvent;
import com.seoltangmyo.sugarcat.domain.insulin.repository.InsulinRecordRepository;
import com.seoltangmyo.sugarcat.domain.meal.entity.MealRecord;
import com.seoltangmyo.sugarcat.domain.meal.event.MealRecordCreatedEvent;
import com.seoltangmyo.sugarcat.domain.meal.repository.MealRecordRepository;
import com.seoltangmyo.sugarcat.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecordNotificationEventListener {

    private final MealRecordRepository mealRecordRepository;
    private final InsulinRecordRepository insulinRecordRepository;
    private final BloodSugarRecordRepository bloodSugarRecordRepository;
    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void handleMealRecordCreated(MealRecordCreatedEvent event) {
        try {
            MealRecord mealRecord = mealRecordRepository.findById(event.recordId())
                    .orElseThrow(() -> new IllegalArgumentException("식사 기록을 찾을 수 없습니다."));

            notificationService.sendMealCompletedToFamily(
                    mealRecord.getCat(),
                    mealRecord.getRecordedBy().getNickname(),
                    mealRecord.getRecordDate(),
                    mealRecord.getSequence()
            );

            log.info("[식사 기록 저장 알림 완료] recordId={}", event.recordId());

        } catch (Exception e) {
            log.error("[식사 기록 저장 알림 실패] recordId={}", event.recordId(), e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void handleInsulinRecordCreated(InsulinRecordCreatedEvent event) {
        try {
            InsulinRecord insulinRecord = insulinRecordRepository.findById(event.recordId())
                    .orElseThrow(() -> new IllegalArgumentException("인슐린 기록을 찾을 수 없습니다."));

            notificationService.sendInsulinCompletedToFamily(
                    insulinRecord.getCat(),
                    insulinRecord.getRecordedBy().getNickname(),
                    insulinRecord.getRecordDate(),
                    insulinRecord.getSequence()
            );

            log.info("[인슐린 기록 저장 알림 완료] recordId={}", event.recordId());

        } catch (Exception e) {
            log.error("[인슐린 기록 저장 알림 실패] recordId={}", event.recordId(), e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void handleBloodSugarRecordCreated(BloodSugarRecordCreatedEvent event) {
        try {
            BloodSugarRecord bloodSugarRecord = bloodSugarRecordRepository.findById(event.recordId())
                    .orElseThrow(() -> new IllegalArgumentException("혈당 기록을 찾을 수 없습니다."));

            notificationService.sendBloodSugarCompletedToFamily(
                    bloodSugarRecord.getCat(),
                    bloodSugarRecord.getRecordedBy().getNickname(),
                    bloodSugarRecord.getSugarValue(),
                    bloodSugarRecord.getSugarStatus(),
                    bloodSugarRecord.getRecordDate(),
                    bloodSugarRecord.getSequence()
            );

            log.info("[혈당 기록 저장 알림 완료] recordId={}", event.recordId());

        } catch (Exception e) {
            log.error("[혈당 기록 저장 알림 실패] recordId={}", event.recordId(), e);
        }
    }
}
