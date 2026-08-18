package com.example.likelion14th_hackathon.recommendation.controller;

import com.example.likelion14th_hackathon.common.api.ApiResponse;
import com.example.likelion14th_hackathon.common.security.JwtTokenProvider;
import com.example.likelion14th_hackathon.recommendation.dto.RecommendationResponse;
import com.example.likelion14th_hackathon.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/schedules/{scheduleId}/recommendation")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final JwtTokenProvider jwtTokenProvider;   // 추가

    @GetMapping
    public ResponseEntity<ApiResponse<RecommendationResponse>> recommend(
            @PathVariable Long scheduleId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        Long memberId = jwtTokenProvider.extractMemberIdFromAuthorization(authorizationHeader);
        RecommendationResponse result =
                recommendationService.recommendForSchedule(scheduleId, memberId);
        return ResponseEntity.ok(ApiResponse.success(result, "추천 제품을 조회했습니다."));
    }
}