package com.example.likelion14th_hackathon.common.chat.service;

public class ChatSessionNotFoundException extends RuntimeException {
    public ChatSessionNotFoundException() {
        super("존재하지 않는 대화 세션입니다.");
    }
}
