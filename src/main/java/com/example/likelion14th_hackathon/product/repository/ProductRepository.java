package com.example.likelion14th_hackathon.product.repository;

import com.example.likelion14th_hackathon.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
