package com.example.likelion14th_hackathon.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "owned_products")
public class OwnedProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "purchased_at")
    private LocalDateTime purchasedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OwnedProductStatus status = OwnedProductStatus.ACTIVE;

    protected OwnedProduct() {
    }

    public OwnedProduct(Long memberId, Product product, LocalDateTime purchasedAt, OwnedProductStatus status) {
        this.memberId = memberId;
        this.product = product;
        this.purchasedAt = purchasedAt;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Product getProduct() {
        return product;
    }

    public LocalDateTime getPurchasedAt() {
        return purchasedAt;
    }

    public OwnedProductStatus getStatus() {
        return status;
    }
}
