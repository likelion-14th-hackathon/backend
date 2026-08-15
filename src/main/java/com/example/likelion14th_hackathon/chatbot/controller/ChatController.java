package com.example.likelion14th_hackathon.chatbot.controller;

import com.example.likelion14th_hackathon.chatbot.service.ChatService;
import com.example.likelion14th_hackathon.common.api.ApiResponse;
import com.example.likelion14th_hackathon.common.chat.dto.ChatRequest;
import com.example.likelion14th_hackathon.common.chat.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products/{productId}/chatbot")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/general") // 상품 일반적인 질문
    public ResponseEntity<ApiResponse<ChatResponse>> generalChat(
            @PathVariable Long productId,
            @RequestBody ChatRequest request) {
        ChatResponse response = chatService.processGeneralChat(productId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "성공"));
    }

    @PostMapping("/keyword") // 단어 롱클릭 질문
    public ResponseEntity<ApiResponse<ChatResponse>> keywordChat(
            @PathVariable Long productId,
            @RequestBody ChatRequest request) {
        ChatResponse response = chatService.processKeywordChat(productId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "성공"));
    }

    @PostMapping("/care-guide") // 제품 관리법 질문
    public ResponseEntity<ApiResponse<ChatResponse>> careGuideChat(
            @PathVariable Long productId,
            @RequestBody ChatRequest request) {
        ChatResponse response = chatService.processCareGuideChat(productId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "성공"));
    }
}
