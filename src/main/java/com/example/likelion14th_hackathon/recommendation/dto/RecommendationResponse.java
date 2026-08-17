package com.example.likelion14th_hackathon.recommendation.dto;

import com.example.likelion14th_hackathon.schedule.entity.ScheduleCategory;
import lombok.Getter;

import java.util.List;

@Getter
public class RecommendationResponse {

    private final Long scheduleId;
    private final ScheduleCategory category;
    private final String title;
    private final List<ProductSummary> ownedProducts;      // "이 제품이 어울려요"
    private final List<ProductSummary> notOwnedProducts;   // "이것도 어울릴 것 같아요"

    public RecommendationResponse(Long scheduleId, ScheduleCategory category, String title,
                                  List<ProductSummary> ownedProducts,
                                  List<ProductSummary> notOwnedProducts) {
        this.scheduleId = scheduleId;
        this.category = category;
        this.title = title;
        this.ownedProducts = ownedProducts;
        this.notOwnedProducts = notOwnedProducts;
    }

    // 추천 제품 한 건
    // 추천 목록에 보여줄 최소한의 정보만 담는 작은 DTO
    @Getter
    public static class ProductSummary {
        private final Long productId;
        private final String name;
        private final String imageUrl;

        public ProductSummary(Long productId, String name, String imageUrl) {
            this.productId = productId;
            this.name = name;
            this.imageUrl = imageUrl;
        }
    }
}