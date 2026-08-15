package com.example.likelion14th_hackathon.common.chat.repository;

import com.example.likelion14th_hackathon.common.chat.domain.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<ChatSession, Long> {
}
