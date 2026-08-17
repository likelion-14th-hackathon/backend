package com.example.likelion14th_hackathon.mypage.presentation.dto;

import com.example.likelion14th_hackathon.mypage.domain.OwnedProduct;
import com.example.likelion14th_hackathon.mypage.domain.OwnershipStatus;

import java.time.LocalDateTime;

public record OwnedProductResponse(
        Long ownedProductId,
        Long productId,
        String productName,
        Integer price,
        String productType,
        String color,
        Long materialId,
        String materialName,
        Integer clothSize,
        String bagSize,
        String description,
        String imageUrl,
        String careInfo,
        LocalDateTime purchasedAt,
        OwnershipStatus status
) {

    public static OwnedProductResponse from(OwnedProduct ownedProduct) {
        return new OwnedProductResponse(
                ownedProduct.getId(),
                ownedProduct.getProduct().getProductId(),
                ownedProduct.getProduct().getName(),
                ownedProduct.getProduct().getPrice(),
                ownedProduct.getProduct().getProductType(),
                ownedProduct.getProduct().getColor(),
                ownedProduct.getProduct().getMaterial().getId(),
                ownedProduct.getProduct().getMaterial().getMaterialName(),
                ownedProduct.getProduct().getClothSize(),
                ownedProduct.getProduct().getBagSize(),
                ownedProduct.getProduct().getDescription(),
                ownedProduct.getProduct().getImageUrl(),
                ownedProduct.getProduct().getCareInfo(),
                ownedProduct.getPurchasedAt(),
                ownedProduct.getStatus()
        );
    }
}
