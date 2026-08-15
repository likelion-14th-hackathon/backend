package com.example.likelion14th_hackathon.common.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRequest {
    // 1. 공통 및 일반 상품 챗봇용 필드
    private Long sessionId;          // 세션 ID (처음엔 null)
    private String message;          // 유저 메시지 (일반 문의, 관리법, AI CARE 질문)
    private String selectedKeyword;  // 단어 롱클릭 챗봇용 키워드

    // 2. AI CARE 및 푸시 알림 연동용 필드
    private Long userProductId;      // 마이페이지 내 제품 ID
    private Long notificationId;     // 푸시 알림 ID
    private String triggerType;      // 푸시 트리거 타입 (예: WEATHER_ALERT, CARE_CYCLE)
}
