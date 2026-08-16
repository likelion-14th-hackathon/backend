package com.example.likelion14th_hackathon.catalog.repository;

import com.example.likelion14th_hackathon.catalog.domain.Product;
import com.example.likelion14th_hackathon.catalog.domain.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStatus(ProductStatus status);

    List<Product> findByStyleCategoryAndStatus(String styleCategory, ProductStatus status);

    List<Product> findByNameContainingAndStatus(String keyword, ProductStatus status);
}