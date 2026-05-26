package com.seoltangmyo.sugarcat.domain.cat.controller;

import com.seoltangmyo.sugarcat.domain.cat.dto.CatExportResponse;
import com.seoltangmyo.sugarcat.domain.cat.service.CatExportService;
import com.seoltangmyo.sugarcat.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cats")
@RequiredArgsConstructor
public class CatController {

    private final CatExportService catExportService;

    @GetMapping("/me/export")
    public ResponseEntity<CatExportResponse> exportRecords(
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @RequestParam("startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam("endDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        UUID userId = userDetails.getUserId();

        CatExportResponse response =
                catExportService.exportRecords(userId, startDate, endDate);

        return ResponseEntity.ok(response);
    }
}
