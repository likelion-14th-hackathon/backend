package com.example.likelion14th_hackathon.history.repository;

import com.example.likelion14th_hackathon.history.entity.PhotoDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PhotoDetailRepository extends JpaRepository<PhotoDetail, Long> {

    Optional<PhotoDetail> findByHistoryItem_HistoryId(Long historyId);

    // 특정 회원의 모든 사진 URL (최신순) - AI 스타일 분석용
    @Query("""
           select pd.photoUrl
           from PhotoDetail pd
           where pd.historyItem.ownedProduct.member.id = :memberId
           order by pd.historyItem.createdAt desc
           """)
    List<String> findPhotoUrlsByMemberId(@Param("memberId") Long memberId);
}