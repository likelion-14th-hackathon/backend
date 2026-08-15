package com.example.likelion14th_hackathon.history.repository;

import com.example.likelion14th_hackathon.product.entity.Product;
import com.example.likelion14th_hackathon.history.entity.HistoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryItemRepository extends JpaRepository<HistoryItem, Long> {
    List<HistoryItem> findByProductOrderByCreatedAtAsc(Product product);
}
