package com.example.likelion14th_hackathon.catalog.service;

import com.example.likelion14th_hackathon.catalog.domain.Material;
import com.example.likelion14th_hackathon.catalog.domain.Product;
import com.example.likelion14th_hackathon.mypage.domain.OwnedProduct;
import com.example.likelion14th_hackathon.style.entity.ProductStyle;
import com.example.likelion14th_hackathon.style.repository.ProductStyleRepository;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProductPromptContextBuilder {

    private final ProductStyleRepository productStyleRepository;

    public ProductPromptContextBuilder(ProductStyleRepository productStyleRepository) {
        this.productStyleRepository = productStyleRepository;
    }

    public String buildProductContext(Product product) {
        if (product == null) {
            return "[상품 정보]\n조회된 상품 정보가 없습니다.";
        }

        Material material = product.getMaterial();
        String styleTypes = product.getProductId() == null
                ? "정보 없음"
                : productStyleRepository.findByProduct_ProductId(product.getProductId()).stream()
                .map(ProductStyle::getStyleType)
                .map(this::value)
                .collect(Collectors.joining(", "));
        if (styleTypes.isBlank()) {
            styleTypes = "정보 없음";
        }

        return """
                [상품 정보]
                id: %s
                name: %s
                price: %s
                productType: %s
                styleTypes: %s
                color: %s
                clothsize: %s
                bagsize: %s
                description: %s
                imageUrl: %s
                careInfo: %s

                [소재 정보]
                materialId: %s
                materialName: %s
                careSummary: %s
                cleaningMethod: %s
                storageMethod: %s
                avoidList: %s
                waterWarning: %s
                repairRecommendation: %s
                """.formatted(
                value(product.getProductId()),
                value(product.getName()),
                value(product.getPrice()),
                value(product.getProductType()),
                styleTypes,
                value(product.getColor()),
                value(product.getClothSize()),
                value(product.getBagSize()),
                value(product.getDescription()),
                value(product.getImageUrl()),
                value(product.getCareInfo()),
                material == null ? "정보 없음" : value(material.getId()),
                material == null ? "정보 없음" : value(material.getMaterialName()),
                material == null ? "정보 없음" : value(material.getCareSummary()),
                material == null ? "정보 없음" : value(material.getCleaningMethod()),
                material == null ? "정보 없음" : value(material.getStorageMethod()),
                material == null ? "정보 없음" : value(material.getAvoidList()),
                material == null ? "정보 없음" : value(material.getWaterWarning()),
                material == null ? "정보 없음" : value(material.getRepairRecommendation())
        );
    }

    public String buildOwnedProductContext(Long ownedProductId, OwnedProduct ownedProduct) {
        if (ownedProduct == null) {
            return """
                    [보유 상품 정보]
                    ownedProductId: %s
                    조회된 보유 상품 정보가 없습니다.
                    """.formatted(value(ownedProductId));
        }

        return """
                [보유 상품 정보]
                ownedProductId: %s
                memberId: %s
                purchasedAt: %s
                status: %s

                %s
                """.formatted(
                value(ownedProduct.getId()),
                value(ownedProduct.getMemberId()),
                value(ownedProduct.getPurchasedAt()),
                value(ownedProduct.getStatus()),
                buildProductContext(ownedProduct.getProduct())
        );
    }

    private String value(Object value) {
        if (value == null) {
            return "정보 없음";
        }
        String text = String.valueOf(value);
        return text.isBlank() ? "정보 없음" : text;
    }
}
