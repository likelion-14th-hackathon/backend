package com.example.likelion14th_hackathon.transfer.application;

import com.example.likelion14th_hackathon.member.domain.Member;
import com.example.likelion14th_hackathon.mypage.domain.OwnedProduct;
import com.example.likelion14th_hackathon.mypage.repository.OwnedProductRepository;
import com.example.likelion14th_hackathon.transfer.domain.Transfer;
import com.example.likelion14th_hackathon.transfer.repository.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferService {

    private final OwnedProductRepository ownedProductRepository;
    private final TransferRepository transferRepository;

    public TransferService(OwnedProductRepository ownedProductRepository, TransferRepository transferRepository) {
        this.ownedProductRepository = ownedProductRepository;
        this.transferRepository = transferRepository;
    }

    @Transactional
    public Transfer requestTransfer(Long ownedProductId, String toEmail) {
        OwnedProduct ownedProduct = getOwnedProduct(ownedProductId);
        ownedProduct.requestTransfer();

        Member fromMember = ownedProduct.getMember();
        return transferRepository.save(new Transfer(ownedProduct, fromMember, toEmail));
    }

    @Transactional
    public Transfer completeTransfer(Long transferId) {
        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new IllegalArgumentException("양도 요청을 찾을 수 없습니다. transferId=" + transferId));

        transfer.getOwnedProduct().completeTransfer();
        transfer.complete();

        return transfer;
    }

    private OwnedProduct getOwnedProduct(Long ownedProductId) {
        return ownedProductRepository.findById(ownedProductId)
                .orElseThrow(() -> new IllegalArgumentException("내 제품을 찾을 수 없습니다. ownedProductId=" + ownedProductId));
    }
}

