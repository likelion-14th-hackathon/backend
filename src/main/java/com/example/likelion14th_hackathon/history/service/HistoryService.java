package com.example.likelion14th_hackathon.history.service;

import com.example.likelion14th_hackathon.common.file.ImageBase64Converter;
import com.example.likelion14th_hackathon.history.dto.HistoryResponse;
import com.example.likelion14th_hackathon.history.dto.PhotoDetailResponse;
import com.example.likelion14th_hackathon.history.entity.HistoryItem;
import com.example.likelion14th_hackathon.history.entity.HistoryItemType;
import com.example.likelion14th_hackathon.history.entity.PhotoDetail;
import com.example.likelion14th_hackathon.history.repository.HistoryItemRepository;
import com.example.likelion14th_hackathon.history.repository.PhotoDetailRepository;
import com.example.likelion14th_hackathon.member.domain.Member;
import com.example.likelion14th_hackathon.mypage.domain.OwnedProduct;
import com.example.likelion14th_hackathon.mypage.domain.OwnershipStatus;
import com.example.likelion14th_hackathon.mypage.repository.OwnedProductRepository;
import com.example.likelion14th_hackathon.style.service.StyleAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryItemRepository historyItemRepository;
    private final PhotoDetailRepository photoDetailRepository;
    private final OwnedProductRepository ownedProductRepository;
    private final StyleAnalysisService styleAnalysisService;
    private final ImageBase64Converter imageBase64Converter;

    // 1. 히스토리 목록 조회
    @Transactional(readOnly = true)
    public List<HistoryResponse> getHistories(Long ownedProductId, Long memberId) {
        OwnedProduct ownedProduct = findOwnedProductOrThrow(ownedProductId);
        verifyOwner(ownedProduct, memberId);

        return historyItemRepository.findByOwnedProductOrderByCreatedAtAsc(ownedProduct).stream()
                .map(item -> {
                    String thumbnailUrl = null;
                    if (item.getHistoryItemType() == HistoryItemType.PHOTO) {
                        thumbnailUrl = photoDetailRepository
                                .findByHistoryItem_HistoryId(item.getHistoryId())
                                .map(PhotoDetail::getPhotoUrl)
                                .orElse(null);
                    }
                    return new HistoryResponse(item, thumbnailUrl);
                })
                .toList();
    }

    // 2. 사진 추가
    @Transactional
    public HistoryResponse addPhotoHistory(Long ownedProductId, Long memberId,
                                           String photoUrl, String userMemo) {
        OwnedProduct ownedProduct = findOwnedProductOrThrow(ownedProductId);
        verifyOwner(ownedProduct, memberId);
        verifyNotLocked(ownedProduct);

        HistoryItem item = new HistoryItem(ownedProduct, HistoryItemType.PHOTO,
                "사진", LocalDateTime.now());
        historyItemRepository.save(item);

        PhotoDetail detail = new PhotoDetail(item, photoUrl, userMemo);
        photoDetailRepository.save(detail);
        photoDetailRepository.flush();   // 방금 저장한 사진도 분석에 포함되도록

        // 사진이 추가될 때마다 회원의 전체 사진으로 스타일 재분석
        analyzeStyle(ownedProduct.getMember(), memberId);

        return new HistoryResponse(item, photoUrl);
    }

    // 3. 사진 상세 조회
    @Transactional(readOnly = true)
    public PhotoDetailResponse getPhotoDetail(Long historyId, Long memberId) {
        PhotoDetail detail = photoDetailRepository.findByHistoryItem_HistoryId(historyId)
                .orElseThrow(() -> new IllegalArgumentException("사진 상세를 찾을 수 없습니다."));
        verifyOwner(detail.getHistoryItem().getOwnedProduct(), memberId);
        return new PhotoDetailResponse(detail);
    }

    // 4. 구매 이벤트 생성 (팀원 호출용)
    @Transactional
    public void createPurchaseHistory(OwnedProduct ownedProduct) {
        HistoryItem item = new HistoryItem(ownedProduct, HistoryItemType.PURCHASE,
                "구매", LocalDateTime.now());
        historyItemRepository.save(item);
    }

    // 5. 양도 이벤트 생성 (팀원 호출용)
    // 상태 변경(completeTransfer)은 팀원의 양도 로직에서 처리하므로 여기선 기록만 남긴다.
    @Transactional
    public void createTransferHistory(OwnedProduct ownedProduct) {
        HistoryItem item = new HistoryItem(ownedProduct, HistoryItemType.TRANSFER,
                "양도", LocalDateTime.now());
        historyItemRepository.save(item);
    }

    // ===== 내부 헬퍼 =====
    private OwnedProduct findOwnedProductOrThrow(Long ownedProductId) {
        return ownedProductRepository.findById(ownedProductId)
                .orElseThrow(() -> new IllegalArgumentException("보유 상품을 찾을 수 없습니다."));
    }

    private void verifyOwner(OwnedProduct ownedProduct, Long memberId) {
        if (!ownedProduct.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("해당 상품에 대한 권한이 없습니다.");
        }
    }

    private void verifyNotLocked(OwnedProduct ownedProduct) {
        if (ownedProduct.getStatus() != OwnershipStatus.OWNED) {
            throw new IllegalStateException("양도 중이거나 양도된 상품에는 히스토리를 추가할 수 없습니다.");
        }
    }

    /** 스타일 분석 (실패해도 사진 등록은 성공 처리) */
    private void analyzeStyle(Member member, Long memberId) {
        try {
            List<String> photoUrls = photoDetailRepository.findPhotoUrlsByMemberId(memberId);
            List<String> base64Images = imageBase64Converter.convertAll(photoUrls);
            styleAnalysisService.analyzeAndUpdate(member, base64Images);
        } catch (Exception e) {
            log.error("스타일 분석 중 오류가 발생했지만 사진 등록은 완료되었습니다. {}", e.getMessage());
        }
    }

}