package com.example.likelion14th_hackathon.catalog.dto;

import com.example.likelion14th_hackathon.catalog.domain.Product;
import lombok.Getter;

@Getter
public class ProductResponse {

    private final Long productId;
    private final String name;
    private final Integer price;
    private final String productType;
    private final String color;
    private final Integer clothSize;
    private final String bagSize;
    private final String description;
    private final String imageUrl;
    private final String careInfo;

    public ProductResponse(
            Long productId,
            String name,
            Integer price,
            String productType,
            String color,
            Integer clothSize,
            String bagSize,
            String description,
            String imageUrl,
            String careInfo
    ) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.productType = productType;
        this.color = color;
        this.clothSize = clothSize;
        this.bagSize = bagSize;
        this.description = description;
        this.imageUrl = imageUrl;
        this.careInfo = careInfo;
    }

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                product.getProductType(),
                product.getColor(),
                product.getClothSize(),
                product.getBagSize(),
                product.getDescription(),
                product.getImageUrl(),
                product.getCareInfo()
        );
    }
}
