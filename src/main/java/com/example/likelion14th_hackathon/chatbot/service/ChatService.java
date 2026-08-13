package com.example.likelion14th_hackathon.chatbot.service;

import com.example.likelion14th_hackathon.catalog.domain.Product;
import com.example.likelion14th_hackathon.catalog.repository.ProductRepository;

import com.example.likelion14th_hackathon.common.chat.domain.ChatMessage;
import com.example.likelion14th_hackathon.common.chat.domain.ChatSession;
import com.example.likelion14th_hackathon.common.chat.dto.ChatRequest;
import com.example.likelion14th_hackathon.common.chat.dto.ChatResponse;
import com.example.likelion14th_hackathon.common.chat.repository.ChatRepository;
import com.example.likelion14th_hackathon.common.chat.repository.SessionRepository;
import com.example.likelion14th_hackathon.common.llm.LlmClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ProductRepository productRepository;
    private final SessionRepository sessionRepository;
    private final ChatRepository chatRepository;
    private final LlmClient llmClient;

// 세션 관리 공통 로직
    private ChatSession getOrCreateSession(Long sessionId) {
        if (sessionId == null) { // 새 세션 생성
            return sessionRepository.save(new ChatSession());
        }
        // 기존 세션 반환
        return sessionRepository.findById(sessionId)
                .orElseGet(() -> sessionRepository.save(new ChatSession()));
    }
    // 대화 히스토리
    // 해당 세션의 이전 대화 내역을 시간순으로 조회하여 프롬프트에 주입할 문맥으로 변환
    private String buildHistoryContext(Long sessionId) {
        List<ChatMessage> history = chatRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        return history.stream()
                .map(m -> m.getRole() + ": " + m.getContent())
                .collect(Collectors.joining("\n"));
    }


    // 일반 문의 챗봇
    @Transactional
    public ChatResponse processGeneralChat(Long productId, ChatRequest request) {
        // 세션 확인 및 유저 메시지 저장
        ChatSession session = getOrCreateSession(request.getSessionId());
        chatRepository.save(new ChatMessage(request.getMessage(), "USER", session));

        // 상품 정보 조회
        Product product = productRepository.findById(productId).orElse(null);
        String productInfo = (product != null) ? "상품명: " + product.getName() + ", 설명: " + product.getDescription() : "";

        // 프롬프트 생성 및 LLM 호출
        String history = buildHistoryContext(session.getId());
        String prompt = "당신은 친절한 쇼핑 도우미입니다.\n" + productInfo + "\n\n[대화 기록]\n" + history + "\n\n사용자: " + request.getMessage();

        String aiReply = llmClient.ask(prompt);

        // AI 응답 저장
        chatRepository.save(new ChatMessage(aiReply, "ASSISTANT", session));

        return new ChatResponse(session.getId(), aiReply);
    }

    // 단어 롱클릭 챗봇
    @Transactional
    public ChatResponse processKeywordChat(Long productId, ChatRequest request) {
        ChatSession session = getOrCreateSession(request.getSessionId());
        String keyword = request.getSelectedKeyword();

        // 2줄 이내 요약 프롬프트 설정
        String prompt = "다음 키워드에 대해 명품 가죽/소품 전문가로서 핵심만 담아 2줄 이내로 아주 간결하게 설명해주세요: " + keyword;
        String shortReply = llmClient.ask(prompt);

        // 키워드 질의응답 내역 저장
        chatRepository.save(new ChatMessage("키워드 설명 요청: " + keyword, "USER", session));
        chatRepository.save(new ChatMessage(shortReply, "ASSISTANT", session));

        return new ChatResponse(session.getId(), keyword, shortReply);
    }


    // 제품 관리법 안내 챗봇
    @Transactional
    public ChatResponse processCareGuideChat(Long productId, ChatRequest request) {
        ChatSession session = getOrCreateSession(request.getSessionId());
        chatRepository.save(new ChatMessage(request.getMessage(), "USER", session));

        Product product = productRepository.findById(productId).orElse(null);
        String productInfo = (product != null) ? "상품명: " + product.getName() + ", 소재/정보: " + product.getDescription() : "";

        String history = buildHistoryContext(session.getId());
        String prompt = "당신은 제품 관리 전문가입니다. 소재에 맞는 올바른 관리법을 안내해주세요.\n" + productInfo + "\n\n[대화 기록]\n" + history + "\n\n사용자: " + request.getMessage();

        String aiReply = llmClient.ask(prompt);
        chatRepository.save(new ChatMessage(aiReply, "ASSISTANT", session));

        return new ChatResponse(session.getId(), aiReply);
    }
}