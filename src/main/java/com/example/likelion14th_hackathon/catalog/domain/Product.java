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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Column(name = "cloth_size")
    private Integer clothSize;

    @Column(name = "bag_size", length = 50)
    private String bagSize;

    @Column(name = "style_category", length = 100)
    private String styleCategory;

    @Column(columnDefinition = "TEXT")
    private String description;

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
            String styleCategory,
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
        this.styleCategory = styleCategory;
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

    public String getStyleCategory() {
        return styleCategory;
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
            String styleCategory,
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
        this.styleCategory = styleCategory;
        this.description = description;
        this.imageUrl = imageUrl;
        this.careInfo = careInfo;
    }
}
