package com.example.likelion14th_hackathon.care.service;

import com.example.likelion14th_hackathon.common.chat.domain.ChatMessage;
import com.example.likelion14th_hackathon.common.chat.domain.ChatSession;
import com.example.likelion14th_hackathon.common.chat.dto.ChatRequest;
import com.example.likelion14th_hackathon.common.chat.dto.ChatResponse;
import com.example.likelion14th_hackathon.common.chat.repository.ChatRepository;
import com.example.likelion14th_hackathon.common.chat.repository.SessionRepository;
import com.example.likelion14th_hackathon.common.chat.service.ChatSessionService;
import com.example.likelion14th_hackathon.common.llm.LlmClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CareService {

    private final SessionRepository sessionRepository;
    private final ChatRepository chatRepository;
    private final ChatSessionService chatSessionService;
    private final LlmClient llmClient;

    @Transactional
    public ChatResponse processCareChat(Long userProductId, ChatRequest request) {
        // 세션 조회 또는 생성 (새로고침/재접속 대응)
        ChatSession session = chatSessionService.getOrCreate(request.getSessionId());

        // 유저 메시지 저장 (순서: role, content, session)
        chatRepository.save(new ChatMessage(request.getMessage(), "USER", session));

        // LLM 프롬프트 생성 및 답변 도출
        String prompt = String.format("등록 제품 ID: %d, 사용자 문의: %s", userProductId, request.getMessage());
        String reply = llmClient.ask(prompt);

        // AI 답변 메시지 저장
        chatRepository.save(new ChatMessage(reply, "ASSISTANT", session));

        // 응답 반환
        return new ChatResponse(session.getId(), reply);
    }

    @Transactional
    public ChatResponse initCareChat(Long userProductId, ChatRequest request) {
        // 푸시 알림 클릭 진입 시 새로운 세션 발급
        ChatSession session = sessionRepository.save(new ChatSession());

        // 트리거 타입에 따른 맞춤형 환영 인사 생성
        // 일단 하드 코딩으로 하고 날씨 api 연동 후 수정 예정
        String triggerType = request.getTriggerType();
        String initialMessage;

        if ("WEATHER_ALERT".equals(triggerType)) {
            initialMessage = "오늘 비 소식이 있어 고객님의 소중한 가죽 가방 관리가 중요해요! 외출 후 부드러운 천으로 닦아주세요.";
        } else if ("CARE_CYCLE".equals(triggerType)) {
            initialMessage = "고객님이 등록하신 제품의 정기 케어 주기가 도래했습니다. 가죽 영양 크림을 도포해 주세요.";
        } else {
            initialMessage = "등록하신 제품과 관련하여 맞춤형 케어 안내를 도와드릴게요!";
        }

        // 초기 환영 인사 메시지 저장
        chatRepository.save(new ChatMessage(initialMessage, "ASSISTANT", session));

        return ChatResponse.ofInit(session.getId(), initialMessage);
    }
}
