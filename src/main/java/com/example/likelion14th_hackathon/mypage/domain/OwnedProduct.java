package com.example.likelion14th_hackathon.mypage.domain;

import com.example.likelion14th_hackathon.catalog.domain.Product;
import com.example.likelion14th_hackathon.member.domain.Member;
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

    // 회원 1명은 여러 OwnedProduct를 가질 수 있다.
    // OwnedProduct 1개는 "특정 회원이 구매해서 보유한 실제 상품 1개"를 의미한다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // Product는 쇼핑몰에 등록된 상품 정보이고,
    // OwnedProduct는 그 상품을 특정 회원이 구매한 소유 기록이다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private LocalDateTime purchasedAt;

    // boolean transferred 대신 enum을 사용한다.
    // 이유: 보유 중/양도 요청 중/양도 완료처럼 2개보다 많은 상태를 명확히 표현하기 위해서다.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OwnershipStatus status = OwnershipStatus.OWNED;

    protected OwnedProduct() {
    }

    public OwnedProduct(Member member, Product product, LocalDateTime purchasedAt) {
        this.member = member;
        this.product = product;
        this.purchasedAt = purchasedAt;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Long getMemberId() {
        return member == null ? null : member.getId();
    }

    public Product getProduct() {
        return product;
    }

    public LocalDateTime getPurchasedAt() {
        return purchasedAt;
    }

    public OwnershipStatus getStatus() {
        return status;
    }

    public void requestTransfer() {
        if (status == OwnershipStatus.TRANSFER_PENDING) {
            throw new IllegalArgumentException("이미 양도 요청 중인 제품입니다.");
        }
        if (status == OwnershipStatus.TRANSFERRED) {
            throw new IllegalArgumentException("이미 양도 완료된 제품입니다.");
        }
        this.status = OwnershipStatus.TRANSFER_PENDING;
    }

    public void completeTransfer() {
        if (status != OwnershipStatus.TRANSFER_PENDING) {
            throw new IllegalArgumentException("양도 진행 중인 제품만 완료 처리할 수 있습니다.");
        }
        this.status = OwnershipStatus.TRANSFERRED;
    }
}

