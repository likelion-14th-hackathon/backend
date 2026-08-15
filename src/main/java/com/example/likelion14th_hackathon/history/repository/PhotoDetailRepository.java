package com.example.likelion14th_hackathon.history.repository;

import com.example.likelion14th_hackathon.history.entity.PhotoDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhotoDetailRepository extends JpaRepository<PhotoDetail, Long> {
    Optional<PhotoDetail> findByHistoryItem_HistoryId(Long historyId);
}
