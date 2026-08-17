package com.example.likelion14th_hackathon.mypage.application;

import com.example.likelion14th_hackathon.mypage.domain.OwnedProduct;
import com.example.likelion14th_hackathon.mypage.repository.OwnedProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MyPageService {

    private final OwnedProductRepository ownedProductRepository;

    public MyPageService(OwnedProductRepository ownedProductRepository) {
        this.ownedProductRepository = ownedProductRepository;
    }

    @Transactional(readOnly = true)
    public List<OwnedProduct> getOwnedProducts(Long memberId) {
        return ownedProductRepository.findByMemberId(memberId);
    }
}
