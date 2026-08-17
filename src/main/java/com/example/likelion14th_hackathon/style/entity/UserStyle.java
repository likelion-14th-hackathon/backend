package com.example.likelion14th_hackathon.style.entity;

import com.example.likelion14th_hackathon.member.domain.Member;   
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class UserStyle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_style_id")
    private Long memberStyleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StyleType styleType;

    protected UserStyle() {}

    public UserStyle(Member member, StyleType styleType) {
        this.member = member;
        this.styleType = styleType;
    }
}