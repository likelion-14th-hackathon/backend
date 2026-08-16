package com.example.likelion14th_hackathon.catalog.repository;

import com.example.likelion14th_hackathon.catalog.domain.OwnedProduct;
import com.example.likelion14th_hackathon.catalog.domain.OwnedProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OwnedProductRepository extends JpaRepository<OwnedProduct, Long> {

    List<OwnedProduct> findByStatus(OwnedProductStatus status);
}
