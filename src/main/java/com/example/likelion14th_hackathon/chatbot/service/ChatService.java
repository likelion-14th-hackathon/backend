package com.example.likelion14th_hackathon.chatbot.service;

import com.example.likelion14th_hackathon.catalog.domain.Product;
import com.example.likelion14th_hackathon.catalog.repository.ProductRepository;

import com.example.likelion14th_hackathon.catalog.service.ProductPromptContextBuilder;
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
    private final ProductPromptContextBuilder productPromptContextBuilder;

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
        String productInfo = productPromptContextBuilder.buildProductContext(product);

        // 프롬프트 생성 및 LLM 호출
        String history = buildHistoryContext(session.getId());
        String prompt = """
                당신은 MCM 명품/패션 상품 상담 챗봇입니다.
                아래 상품 정보와 소재 정보를 기준으로만 답변하세요.
                정보가 없는 필드는 추측하지 말고 "확인된 정보가 없습니다"라고 안내하세요.
                가격, 타입, 색상, 사이즈, 스타일, 설명, 이미지, 케어 정보가 질문과 관련 있으면 함께 반영하세요.

                %s

                [대화 기록]
                %s

                사용자: %s
                """.formatted(productInfo, history, request.getMessage());

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
        Product product = productRepository.findById(productId).orElse(null);
        String productInfo = productPromptContextBuilder.buildProductContext(product);

        // 2줄 이내 요약 프롬프트 설정
        String prompt = """
                당신은 MCM 명품/패션 상품 용어를 짧게 설명하는 전문가입니다.
                아래 상품 정보와 소재 정보를 참고해, 선택된 키워드를 2줄 이내로 아주 간결하게 설명하세요.
                정보가 부족하면 일반론과 상품에 확인된 정보를 구분해서 답변하세요.

                %s

                선택된 키워드: %s
                """.formatted(productInfo, keyword);
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
        String productInfo = productPromptContextBuilder.buildProductContext(product);

        String history = buildHistoryContext(session.getId());
        String prompt = """
                당신은 MCM 명품/패션 제품 관리 전문가입니다.
                아래 상품 정보와 소재 정보의 careInfo, careSummary, cleaningMethod, storageMethod, avoidList, waterWarning, repairRecommendation을 우선 기준으로 답변하세요.
                상품의 productType, color, clothsize, bagsize, styleCategory도 관리 안내에 필요하면 반영하세요.
                확인되지 않은 소재나 관리법은 단정하지 말고 전문 수선점 확인을 권하세요.

                %s

                [대화 기록]
                %s

                사용자: %s
                """.formatted(productInfo, history, request.getMessage());

        String aiReply = llmClient.ask(prompt);
        chatRepository.save(new ChatMessage(aiReply, "ASSISTANT", session));

        return new ChatResponse(session.getId(), aiReply);
    }
}
