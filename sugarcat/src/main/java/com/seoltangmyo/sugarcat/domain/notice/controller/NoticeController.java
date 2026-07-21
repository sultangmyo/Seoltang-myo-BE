package com.seoltangmyo.sugarcat.domain.notice.controller;

import com.seoltangmyo.sugarcat.domain.notice.dto.NoticeResponse;
import com.seoltangmyo.sugarcat.domain.notice.model.NoticeContent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notices")
public class NoticeController {

    @GetMapping("/active")
    public ResponseEntity<NoticeResponse> getActiveNotice() {
        return ResponseEntity.ok(NoticeContent.activeNotice());
    }
}
