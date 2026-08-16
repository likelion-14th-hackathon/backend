package com.example.likelion14th_hackathon.care.controller;

import com.example.likelion14th_hackathon.care.service.CareService;
import com.example.likelion14th_hackathon.common.api.ApiResponse;
import com.example.likelion14th_hackathon.common.chat.dto.ChatRequest;
import com.example.likelion14th_hackathon.common.chat.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mypage/care")
@RequiredArgsConstructor
public class CareController {

    private final CareService careService;

     // 내 제품 관리법 일반 챗봇
    @PostMapping("/{userProductId}/chat")
    public ResponseEntity<ApiResponse<ChatResponse>> chatWithCare(
            @PathVariable Long userProductId,
            @RequestBody ChatRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(careService.processCareChat(userProductId, request), "성공"));
    }


     // 푸시 알림 연동 챗봇 초기 진입 (Init)

    @PostMapping("/{userProductId}/chat/init")
    public ResponseEntity<ApiResponse<ChatResponse>> initCareChat(
            @PathVariable Long userProductId,
            @RequestBody ChatRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(careService.initCareChat(userProductId, request), "성공"));
    }
}
