package com.seoltangmyo.sugarcat.domain.notice.controller;

import com.seoltangmyo.sugarcat.domain.notice.dto.NoticeResponse;
import com.seoltangmyo.sugarcat.domain.notice.model.NoticeContent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Notice", description = "공지사항 API")
@RestController
@RequestMapping("/api/v1/notices")
public class NoticeController {

    @Operation(summary = "공지 활성화")
    @GetMapping("/active")
    public ResponseEntity<NoticeResponse> getActiveNotice() {
        return ResponseEntity.ok(NoticeContent.activeNotice());
    }
}
