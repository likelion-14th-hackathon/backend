package com.example.likelion14th_hackathon.transfer.domain;

import com.example.likelion14th_hackathon.member.domain.Member;
import com.example.likelion14th_hackathon.mypage.domain.OwnedProduct;
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
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owned_product_id", nullable = false)
    private OwnedProduct ownedProduct;

    // 양도 요청을 만든 회원이다.
    // 현재 목업에서는 양도받는 사람을 toEmail 문자열로만 받는다.
    // 실제 회원 간 양도로 확장하면 toMember 필드를 Member 연관관계로 추가하면 된다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_member_id", nullable = false)
    private Member fromMember;

    private String toEmail;

    @Enumerated(EnumType.STRING)
    private TransferStatus status = TransferStatus.REQUESTED;

    private LocalDateTime requestedAt;

    private LocalDateTime completedAt;

    protected Transfer() {
    }

    public Transfer(OwnedProduct ownedProduct, Member fromMember, String toEmail) {
        this.ownedProduct = ownedProduct;
        this.fromMember = fromMember;
        this.toEmail = toEmail;
        this.requestedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public OwnedProduct getOwnedProduct() {
        return ownedProduct;
    }

    public Member getFromMember() {
        return fromMember;
    }

    public String getToEmail() {
        return toEmail;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void complete() {
        this.status = TransferStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
}
