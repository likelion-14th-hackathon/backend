package com.example.likelion14th_hackathon.transfer.repository;

import com.example.likelion14th_hackathon.transfer.domain.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

    List<Transfer> findByOwnedProductId(Long ownedProductId);
}
