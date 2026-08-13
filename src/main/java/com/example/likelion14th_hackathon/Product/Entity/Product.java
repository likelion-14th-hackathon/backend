package com.example.likelion14th_hackathon.Product.Entity;

import com.example.likelion14th_hackathon.User.Entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;   // 이 상품의 소유자 (N:1)

    @Column(nullable = false)
    private String name;   // 상품명

    protected Product() {
        // JPA 기본 생성자
    }

    public Product(User user, String name) {
        this.user = user;
        this.name = name;
    }
}
