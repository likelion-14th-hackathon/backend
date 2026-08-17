package com.example.likelion14th_hackathon.history.entity;

import com.example.likelion14th_hackathon.mypage.domain.OwnedProduct;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class HistoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owned_product_id", nullable = false)
    private OwnedProduct ownedProduct;   // Product → OwnedProduct

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HistoryItemType historyItemType;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected HistoryItem() {}

    public HistoryItem(OwnedProduct ownedProduct, HistoryItemType historyItemType,
                       String title, LocalDateTime createdAt) {
        this.ownedProduct = ownedProduct;
        this.historyItemType = historyItemType;
        this.title = title;
        this.createdAt = createdAt;
    }
}