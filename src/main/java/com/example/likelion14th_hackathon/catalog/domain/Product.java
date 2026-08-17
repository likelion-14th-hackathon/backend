package com.example.likelion14th_hackathon.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "product_type", nullable = false, length = 50)
    private String productType;

    @Column(length = 50)
    private String color;

    // 소재는 여러 상품에서 재사용되므로 별도 materials 테이블로 분리한다.
    // Product 여러 개가 하나의 Material을 참조하는 N:1 관계다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Column(name = "cloth_size")
    private Integer clothSize;

    @Column(name = "bag_size", length = 50)
    private String bagSize;

    @Column(columnDefinition = "TEXT")
    private String description;

    // 이미지 파일은 S3에 저장하고, DB에는 S3 URL만 저장한다.
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "care_info", columnDefinition = "TEXT")
    private String careInfo;

    protected Product() {
    }

    public Product(
            String name,
            Integer price,
            String productType,
            String color,
            Material material,
            Integer clothSize,
            String bagSize,
            String description,
            String imageUrl,
            String careInfo
    ) {
        this.name = name;
        this.price = price;
        this.productType = productType;
        this.color = color;
        this.material = material;
        this.clothSize = clothSize;
        this.bagSize = bagSize;
        this.description = description;
        this.imageUrl = imageUrl;
        this.careInfo = careInfo;
    }

    public Long getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public Integer getPrice() {
        return price;
    }

    public String getProductType() {
        return productType;
    }

    public String getColor() {
        return color;
    }

    public Material getMaterial() {
        return material;
    }

    public Integer getClothSize() {
        return clothSize;
    }

    public String getBagSize() {
        return bagSize;
    }


    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCareInfo() {
        return careInfo;
    }

    public void updateDetail(
            String name,
            Integer price,
            String productType,
            String color,
            Material material,
            Integer clothSize,
            String bagSize,
            String description,
            String imageUrl,
            String careInfo
    ) {
        this.name = name;
        this.price = price;
        this.productType = productType;
        this.color = color;
        this.material = material;
        this.clothSize = clothSize;
        this.bagSize = bagSize;
        this.description = description;
        this.imageUrl = imageUrl;
        this.careInfo = careInfo;
    }
}
