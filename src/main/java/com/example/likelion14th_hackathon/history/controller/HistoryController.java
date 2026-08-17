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
@RequestMapping("/mypage/owned-products/{ownedProductId}")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<HistoryResponse>>> getHistories(
            @PathVariable Long ownedProductId,
            @RequestHeader("X-User-Id") Long memberId
    ) {
        List<HistoryResponse> result = historyService.getHistories(ownedProductId, memberId);
        return ResponseEntity.ok(ApiResponse.success(result, "히스토리 목록을 조회했습니다."));
    }

    @PostMapping("/photo")
    public ResponseEntity<ApiResponse<HistoryResponse>> addPhoto(
            @PathVariable Long ownedProductId,
            @RequestHeader("X-User-Id") Long memberId,
            @RequestParam("photo") MultipartFile photo,
            @RequestParam(value = "userMemo", required = false) String userMemo
    ) {
        String photoUrl = fileStorageService.store(photo);
        HistoryResponse result = historyService.addPhotoHistory(
                ownedProductId, memberId, photoUrl, userMemo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(result, "사진을 등록했습니다."));
    }

    @GetMapping("/histories/{historyId}")
    public ResponseEntity<ApiResponse<PhotoDetailResponse>> getPhotoDetail(
            @PathVariable Long ownedProductId,
            @PathVariable Long historyId,
            @RequestHeader("X-User-Id") Long memberId
    ) {
        PhotoDetailResponse result = historyService.getPhotoDetail(historyId, memberId);
        return ResponseEntity.ok(ApiResponse.success(result, "사진 상세를 조회했습니다."));
    }
}