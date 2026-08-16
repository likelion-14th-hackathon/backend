package com.example.likelion14th_hackathon.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "product_type", length = 50)
    private String productType;

    @Column(length = 50)
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id")
    private MaterialInfo material;

    @Column(name = "clothsize")
    private Integer clothsize;

    @Column(name = "bagsize", length = 50)
    private String bagsize;

    @Column(name = "style_category", length = 100)
    private String styleCategory;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "care_info", columnDefinition = "TEXT")
    private String careInfo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status = ProductStatus.ACTIVE;

    protected Product() {
    }

    public Product(
            String name,
            Integer price,
            String productType,
            String color,
            MaterialInfo material,
            Integer clothsize,
            String bagsize,
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
        this.clothsize = clothsize;
        this.bagsize = bagsize;
        this.styleCategory = styleCategory;
        this.description = description;
        this.imageUrl = imageUrl;
        this.careInfo = careInfo;
    }

    public Long getId() {
        return id;
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

    public MaterialInfo getMaterial() {
        return material;
    }

    public Integer getClothsize() {
        return clothsize;
    }

    public String getBagsize() {
        return bagsize;
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

    public ProductStatus getStatus() {
        return status;
    }

    public void updateDetail(
            String name,
            Integer price,
            String productType,
            String color,
            MaterialInfo material,
            Integer clothsize,
            String bagsize,
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
        this.clothsize = clothsize;
        this.bagsize = bagsize;
        this.styleCategory = styleCategory;
        this.description = description;
        this.imageUrl = imageUrl;
        this.careInfo = careInfo;
    }
}
