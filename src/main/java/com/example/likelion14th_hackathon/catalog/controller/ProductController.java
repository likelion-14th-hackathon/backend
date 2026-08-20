package com.example.likelion14th_hackathon.catalog.controller;

import com.example.likelion14th_hackathon.catalog.dto.ProductResponse;
import com.example.likelion14th_hackathon.catalog.service.ProductService;
import com.example.likelion14th_hackathon.common.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/products", "/api/products"})
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {

        List<ProductResponse> products = productService.getAllProducts();

        return ResponseEntity.ok(
                ApiResponse.success(products, "전체 상품을 조회했습니다.")
        );
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(
            @PathVariable Long productId
    ) {

        ProductResponse product = productService.getProduct(productId);

        return ResponseEntity.ok(
                ApiResponse.success(product, "상품을 조회했습니다.")
        );
    }
}
