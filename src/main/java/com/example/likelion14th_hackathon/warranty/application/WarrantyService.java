package com.example.likelion14th_hackathon.warranty.application;

import com.example.likelion14th_hackathon.mypage.domain.OwnedProduct;
import com.example.likelion14th_hackathon.mypage.repository.OwnedProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
public class WarrantyService {

    private final OwnedProductRepository ownedProductRepository;

    public WarrantyService(OwnedProductRepository ownedProductRepository) {
        this.ownedProductRepository = ownedProductRepository;
    }

    public WarrantyResponse getMockWarranty(Long ownedProductId) {
        OwnedProduct ownedProduct = ownedProductRepository.findById(ownedProductId)
                .orElseThrow(() -> new IllegalArgumentException("내 제품을 찾을 수 없습니다. ownedProductId=" + ownedProductId));

        return new WarrantyResponse(
                "MOCK-WARRANTY-" + ownedProduct.getId(),
                ownedProduct.getProduct().getName(),
                ownedProduct.getMember().getNickname(),
                LocalDate.now().minusMonths(3),
                LocalDate.now().plusYears(1),
                "목업 보증서입니다. 실제 보증서 DB 저장 없이 화면 확인용으로 반환합니다."
        );
    }

    public record WarrantyResponse(
            String warrantyNumber,
            String productName,
            String ownerName,
            LocalDate issuedAt,
            LocalDate expiredAt,
            String description
    ) {
    }
}

