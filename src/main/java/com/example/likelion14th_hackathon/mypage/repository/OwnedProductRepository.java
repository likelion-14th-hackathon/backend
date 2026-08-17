package com.example.likelion14th_hackathon.mypage.repository;

import com.example.likelion14th_hackathon.mypage.domain.OwnedProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OwnedProductRepository extends JpaRepository<OwnedProduct, Long> {

    List<OwnedProduct> findByMemberId(Long memberId);
}
