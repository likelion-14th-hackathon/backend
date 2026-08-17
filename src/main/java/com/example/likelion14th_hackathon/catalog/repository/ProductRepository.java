package com.example.likelion14th_hackathon.catalog.repository;

import com.example.likelion14th_hackathon.catalog.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContaining(String keyword);
}
