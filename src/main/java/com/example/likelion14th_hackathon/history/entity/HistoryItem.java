package com.example.likelion14th_hackathon.history.entity;

import com.example.likelion14th_hackathon.product.entity.Product;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.Getter;

import java.time.LocalDateTime;


@Entity
@Getter
public class HistoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "history_id")
    private Long historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HistoryItemType historyItemType;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected HistoryItem() {
        // JPA 기본 생성자
    }

    public HistoryItem(Product product, HistoryItemType historyItemType, String title, LocalDateTime createdAt) {
        this.product = product;
        this.historyItemType = historyItemType;
        this.title = title;
        this.createdAt = createdAt;
    }




}
