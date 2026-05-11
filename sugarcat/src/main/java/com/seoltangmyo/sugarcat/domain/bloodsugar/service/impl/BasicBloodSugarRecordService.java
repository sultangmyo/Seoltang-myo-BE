package com.seoltangmyo.sugarcat.domain.bloodsugar.service.impl;

import com.seoltangmyo.sugarcat.domain.bloodsugar.repository.BloodSugarRecordRepository;
import com.seoltangmyo.sugarcat.domain.bloodsugar.service.BloodSugarRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicBloodSugarRecordService implements BloodSugarRecordService {

    private final BloodSugarRecordRepository bloodSugarRecordRepository;



}
