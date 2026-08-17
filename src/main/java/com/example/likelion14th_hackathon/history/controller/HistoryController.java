package com.example.likelion14th_hackathon.history.controller;

import com.example.likelion14th_hackathon.common.api.ApiResponse;
import com.example.likelion14th_hackathon.common.file.FileStorageService;
import com.example.likelion14th_hackathon.history.dto.HistoryResponse;
import com.example.likelion14th_hackathon.history.dto.PhotoDetailResponse;
import com.example.likelion14th_hackathon.history.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/products/{productId}/histories")
@RequiredArgsConstructor
public class HistoryController {

    private HistoryService historyService;
    private final FileStorageService fileStorageService;

    //1. 히스토리 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<HistoryResponse>>> getHistories(
            @PathVariable Long productId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        List<HistoryResponse> result = historyService.getHistories(productId, userId);
        return ResponseEntity.ok(
                ApiResponse.success(result, "히스토리 목록을 조회했습니다.")
        );
    }

    // 2. 사진 추가
    @PostMapping("/photo")
    public ResponseEntity<ApiResponse<HistoryResponse>> createPhotoHistory(
            @PathVariable Long productId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam("photo") MultipartFile photo,
            @RequestParam(value = "userMemo", required = false) String userMemo
    ) {
        String photoUrl = fileStorageService.store(photo);   // 실제 저장!

        HistoryResponse result = historyService.createPhotoHistory(productId, userId, photoUrl, userMemo);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.created(result, "사진을 등록했습니다.")
        );
    }

    // 3. 사진 상세 조회
    @GetMapping("/{historyId}/photo")
    public ResponseEntity<ApiResponse<PhotoDetailResponse>> getPhotoDetail(
            @PathVariable Long productId,
            @PathVariable Long historyId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        PhotoDetailResponse result = historyService.getPhotoDetail(historyId, userId);
        return ResponseEntity.ok(
                ApiResponse.success(result, "사진 상세를 조회했습니다.")
        );
    }
}
