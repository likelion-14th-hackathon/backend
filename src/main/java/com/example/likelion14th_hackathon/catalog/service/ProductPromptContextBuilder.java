package com.example.likelion14th_hackathon.catalog.service;

import com.example.likelion14th_hackathon.catalog.domain.MaterialInfo;
import com.example.likelion14th_hackathon.catalog.domain.OwnedProduct;
import com.example.likelion14th_hackathon.catalog.domain.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductPromptContextBuilder {

    public String buildProductContext(Product product) {
        if (product == null) {
            return "[상품 정보]\n조회된 상품 정보가 없습니다.";
        }

        MaterialInfo material = product.getMaterial();

        return """
                [상품 정보]
                id: %s
                name: %s
                price: %s
                productType: %s
                color: %s
                clothsize: %s
                bagsize: %s
                styleCategory: %s
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
                value(product.getId()),
                value(product.getName()),
                value(product.getPrice()),
                value(product.getProductType()),
                value(product.getColor()),
                value(product.getClothsize()),
                value(product.getBagsize()),
                value(product.getStyleCategory()),
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
