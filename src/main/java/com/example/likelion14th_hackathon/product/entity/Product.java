package com.example.likelion14th_hackathon.product.entity;

import com.example.likelion14th_hackathon.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import lombok.Getter;

@Entity
@Getter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;   // 이 상품의 소유자 (N:1)

    @Column(nullable = false)
    private String name;   // 상품명

    protected Product() {
        // JPA 기본 생성자
    }

    public Product(Member member, String name) {
        this.member = member;
        this.name = name;
    }
}
