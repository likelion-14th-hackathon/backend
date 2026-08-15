package com.example.likelion14th_hackathon.common.chat.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ChatSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;

    // 앤티티가 DB에 insert 되기 전에 자동으로 현재 시간으로 채워짐
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
