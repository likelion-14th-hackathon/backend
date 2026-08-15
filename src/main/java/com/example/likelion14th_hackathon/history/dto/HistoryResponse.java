package com.example.likelion14th_hackathon.history.dto;

import com.example.likelion14th_hackathon.history.entity.HistoryItem;
import com.example.likelion14th_hackathon.history.entity.HistoryItemType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class HistoryResponse {

    private final Long historyId;
    private final Long productId;
    private final HistoryItemType historyItemType;
    private final String title;
    private final String thumbnailUrl;   // PHOTO면 사진 URL, 아니면 null
    private final boolean clickable;     // 저장 안 하고 type으로 계산
    private final LocalDateTime createdAt;

    public HistoryResponse(HistoryItem item, String thumbnailUrl) {
        this.historyId = item.getHistoryId();
        this.productId = item.getProduct().getProductId();
        this.historyItemType = item.getHistoryItemType();
        this.title = item.getTitle();
        this.thumbnailUrl = thumbnailUrl;
        this.clickable = (item.getHistoryItemType() == HistoryItemType.PHOTO);
        this.createdAt = item.getCreatedAt();
    }
}