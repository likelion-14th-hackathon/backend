package com.example.likelion14th_hackathon.style.repository;

import com.example.likelion14th_hackathon.style.entity.ProductStyle;
import com.example.likelion14th_hackathon.style.entity.StyleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductStyleRepository extends JpaRepository<ProductStyle, Long> {

    // TPO 규칙으로 나온 스타일들을 가진 제품 매핑 조회
    List<ProductStyle> findByStyleTypeIn(List<StyleType> styleTypes);
}