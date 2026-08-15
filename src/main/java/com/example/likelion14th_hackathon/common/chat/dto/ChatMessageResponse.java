package com.example.likelion14th_hackathon.common.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatMessageResponse {
    private Long messageId;
    private String role;
    private String content;
    private LocalDateTime createdAt;
}
