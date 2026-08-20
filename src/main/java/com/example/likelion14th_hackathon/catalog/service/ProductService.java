package com.example.likelion14th_hackathon.catalog.service;

import com.example.likelion14th_hackathon.catalog.domain.Product;
import com.example.likelion14th_hackathon.catalog.dto.ProductResponse;
import com.example.likelion14th_hackathon.catalog.repository.ProductRepository;
import com.example.likelion14th_hackathon.common.api.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductResponse::from)
                .toList();
    }

    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("상품을 찾을 수 없습니다.")
                );

        return ProductResponse.from(product);
    }
}
