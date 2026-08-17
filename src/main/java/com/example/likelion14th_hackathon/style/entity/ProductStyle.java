package com.example.likelion14th_hackathon.style.entity;

import com.example.likelion14th_hackathon.product.entity.Product;   // TODO: 실제 경로 확인
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ProductStyle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_style_id")
    private Long productStyleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StyleType styleType;

    protected ProductStyle() {}

    public ProductStyle(Product product, StyleType styleType) {
        this.product = product;
        this.styleType = styleType;
    }
}