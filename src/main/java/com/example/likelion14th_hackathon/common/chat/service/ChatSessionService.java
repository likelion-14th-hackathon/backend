package com.example.likelion14th_hackathon.common.chat.service;

import com.example.likelion14th_hackathon.common.chat.domain.ChatSession;
import com.example.likelion14th_hackathon.common.chat.dto.ChatMessageResponse;
import com.example.likelion14th_hackathon.common.chat.repository.ChatRepository;
import com.example.likelion14th_hackathon.common.chat.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatSessionService {
    private final SessionRepository sessionRepository;
    private final ChatRepository chatRepository;

    @Transactional
    public ChatSession getOrCreate(Long sessionId) {
        if (sessionId == null) {
            return sessionRepository.save(new ChatSession());
        }
        return sessionRepository.findById(sessionId)
                .orElseGet(() -> sessionRepository.save(new ChatSession()));
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessages(Long sessionId) {
        if (!sessionRepository.existsById(sessionId)) {
            throw new ChatSessionNotFoundException();
        }
        return chatRepository.findBySessionIdOrderByCreatedAtAsc(sessionId).stream()
                .map(message -> new ChatMessageResponse(
                        message.getId(), message.getRole(), message.getContent(), message.getCreatedAt()))
                .toList();
    }

    @Transactional
    public void deleteSession(Long sessionId) {
        if (!sessionRepository.existsById(sessionId)) {
            throw new ChatSessionNotFoundException();
        }
        chatRepository.deleteBySessionId(sessionId);
        sessionRepository.deleteById(sessionId);
    }
}
