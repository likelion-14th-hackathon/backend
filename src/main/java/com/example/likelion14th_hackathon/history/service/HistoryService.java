package com.example.likelion14th_hackathon.history.service;

import com.example.likelion14th_hackathon.history.dto.HistoryResponse;
import com.example.likelion14th_hackathon.history.dto.PhotoDetailResponse;
import com.example.likelion14th_hackathon.history.entity.HistoryItemType;
import com.example.likelion14th_hackathon.history.entity.PhotoDetail;
import com.example.likelion14th_hackathon.product.*;
import com.example.likelion14th_hackathon.history.entity.HistoryItem;
import com.example.likelion14th_hackathon.history.repository.HistoryItemRepository;
import com.example.likelion14th_hackathon.history.repository.PhotoDetailRepository;
import com.example.likelion14th_hackathon.product.entity.Product;
import com.example.likelion14th_hackathon.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryItemRepository historyItemRepository;
    private final PhotoDetailRepository photoDetailRepository;
    private final ProductRepository productRepository;

    //1. 히스토리 조회
    @Transactional
    public List<HistoryResponse> getHistories(Long productId, Long userId){
        Product product = findProductOrThrow(productId);
        verifyOwner(product, userId);

        return historyItemRepository.findByProductOrderByCreatedAtAsc(product).stream()
                .map(item -> {
                    // 사진 이벤트면 연결된 PhotoDetail의 URL을 썸네일로, 아니면 null
                    String thumbnailUrl = null;
                    if (item.getHistoryItemType() == HistoryItemType.PHOTO) {
                        thumbnailUrl = photoDetailRepository.findById(item.getHistoryId())
                                .map(PhotoDetail::getPhotoUrl)
                                .orElse(null);
                    }
                    return new HistoryResponse(item, thumbnailUrl);
                })
                .toList();
    }

    // 2. 사진 추가
    @Transactional
    public HistoryResponse createPhotoHistory(Long productId, Long userId, String photoUrl, String userMemo) {
        Product product = findProductOrThrow(productId);
        verifyOwner(product, userId);
        verifyNotLocked(product);

        HistoryItem item = new HistoryItem(product, HistoryItemType.PHOTO, "사진", LocalDateTime.now());
        historyItemRepository.save(item);

        PhotoDetail detail = new PhotoDetail(item, photoUrl, userMemo);
        photoDetailRepository.save(detail);

        return new HistoryResponse(item, photoUrl);
    }

    // 3. 사진 상세 조회
    @Transactional(readOnly = true)
    public PhotoDetailResponse getPhotoDetail(Long historyId, Long userId) {
        PhotoDetail detail = photoDetailRepository.findByHistoryItem_HistoryId(historyId)
                .orElseThrow(() -> new IllegalArgumentException("사진 상세를 찾을 수 없습니다."));

        // 이 사진이 속한 상품의 주인이 요청자와 같은지 확인
        verifyOwner(detail.getHistoryItem().getProduct(), userId);

        return new PhotoDetailResponse(detail);
    }

    // 4. 구매 이벤트 생성 (팀원 호출용)
    @Transactional
    public void createPurchaseHistory(Product product) {
        HistoryItem item = new HistoryItem(product, HistoryItemType.PURCHASE, "구매", LocalDateTime.now());
        historyItemRepository.save(item);
    }

    // 5. 양도 이벤트 생성 (팀원 호출용) + 잠금
    @Transactional
    public void createTransferHistory(Product product) {
        HistoryItem item = new HistoryItem(product, HistoryItemType.TRANSFER, "양도", LocalDateTime.now());
        historyItemRepository.save(item);
        product.markAsTransferred(); // TODO: 민주님이 trnasferred=true를 어디에 넣으실지에 따라 결정
    }

    // ===== 내부 헬퍼 =====
    private Product findProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    }

    private void verifyOwner(Product product, Long userId) {
        if (!product.getUser().getUserId().equals(userId)) {
            throw new IllegalStateException("해당 상품에 대한 권한이 없습니다.");
        }
    }

    private void verifyNotLocked(Product product) {
        if (product.isTransferred()) { // TODO: 여기도
            throw new IllegalStateException("양도된 상품에는 히스토리를 추가할 수 없습니다.");
        }
    }


}
