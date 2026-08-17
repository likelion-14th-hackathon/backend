package com.example.likelion14th_hackathon.common.chat.controller;

import com.example.likelion14th_hackathon.common.api.ApiResponse;
import com.example.likelion14th_hackathon.common.chat.dto.ChatMessageResponse;
import com.example.likelion14th_hackathon.common.chat.service.ChatSessionNotFoundException;
import com.example.likelion14th_hackathon.common.chat.service.ChatSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chatbot/session")
@RequiredArgsConstructor
public class ChatSessionController {
    private final ChatSessionService chatSessionService;

    @GetMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getHistory(@PathVariable Long sessionId) {
        try {
            return ResponseEntity.ok(ApiResponse.success(chatSessionService.getMessages(sessionId), "성공"));
        } catch (ChatSessionNotFoundException exception) {
            return ResponseEntity.status(404).body(ApiResponse.<List<ChatMessageResponse>>failure(404, exception.getMessage()));
        }
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<Void>> deleteSession(@PathVariable Long sessionId) {
        try {
            chatSessionService.deleteSession(sessionId);
            return ResponseEntity.ok(ApiResponse.success(null, "세션이 성공적으로 삭제되었습니다."));
        } catch (ChatSessionNotFoundException exception) {
            return ResponseEntity.status(404).body(ApiResponse.failure(404, "삭제할 세션을 찾을 수 없습니다."));
        }
    }
}
