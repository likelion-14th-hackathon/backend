package com.example.likelion14th_hackathon.User.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;   // 로그인 식별용

    @Column(nullable = false)
    private String password;   // 실제로는 암호화(해시)해서 저장

    @Column(nullable = false)
    private String nickname;   // 표시용 이름 (필요 없으면 삭제)

    protected User() {
        // JPA 기본 생성자
    }

    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}