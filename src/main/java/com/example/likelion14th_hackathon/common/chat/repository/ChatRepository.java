package com.example.likelion14th_hackathon.common.chat.repository;

import com.example.likelion14th_hackathon.common.chat.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<ChatMessage, Long> {
    // 특정 세션의 대화 기록을 시간순으로 조회
    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(Long sessionId);

    void deleteBySessionId(Long sessionId);
}
