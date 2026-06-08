package com.seoltangmyo.sugarcat.domain.cat.service;

import com.seoltangmyo.sugarcat.domain.cat.dto.CatExportResponse;

import java.time.LocalDate;
import java.util.UUID;

public interface CatExportService {

    CatExportResponse exportRecords(UUID userId, LocalDate startDate, LocalDate endDate);
}
