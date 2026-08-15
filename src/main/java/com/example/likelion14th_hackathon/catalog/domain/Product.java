package com.example.likelion14th_hackathon.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Column(length = 50)
    private String color;

    @Column(length = 100)
    private String material;

    // 추천 시스템에서 사용하는 패션 스타일 분류다.
    // 예: 비즈니스 캐주얼, 오피스/포멀, 스트릿, 미니멀
    @Column(name = "style_category", length = 100)
    private String styleCategory;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status = ProductStatus.ACTIVE;

    protected Product() {
    }

    public Product(
            String name,
            Integer price,
            String color,
            String material,
            String styleCategory,
            String description,
            String imageUrl
    ) {
        this.name = name;
        this.price = price;
        this.color = color;
        this.material = material;
        this.styleCategory = styleCategory;
        this.description = description;
        this.imageUrl = imageUrl;
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

    public String getColor() {
        return color;
    }

    public String getMaterial() {
        return material;
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

    public ProductStatus getStatus() {
        return status;
    }

    public void updateDetail(
            String name,
            Integer price,
            String color,
            String material,
            String styleCategory,
            String description,
            String imageUrl
    ) {
        this.name = name;
        this.price = price;
        this.color = color;
        this.material = material;
        this.styleCategory = styleCategory;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
