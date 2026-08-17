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
import com.example.likelion14th_hackathon.common.api.ResourceNotFoundException;
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
        Product product = getProduct(productId);
        String productInfo = productPromptContextBuilder.buildProductContext(product);

        // 프롬프트 생성 및 LLM 호출
        String history = buildHistoryContext(session.getId());
        String prompt = """
                당신은 MCM 명품/패션 상품 상담 챗봇입니다.
                아래 상품 정보와 소재 정보는 답변의 내부 참고 자료입니다.
                고객에게 "DB", "데이터베이스", "근거 자료", "확인된 정보" 같은 내부 표현을 말하지 마세요.
                단순히 정보를 다시 나열하지 말고, 상품명, 가격, 타입, 색상, 사이즈, 스타일, 소재, 설명, 케어 정보를 종합해서 고객 질문에 맞는 판단과 추천을 해주세요.
                제공된 상품 정보에 있는 사실은 자연스럽게 활용하고, 그 사실에서 이어지는 해석은 상담 의견처럼 말해도 됩니다.
                제공된 상품 정보만으로 답변이 빈약하면, MCM 제품군 안에서 일반적으로 통용되는 설명을 "보통", "일반적으로", "MCM의 이런 유형 제품은"처럼 표현해 보완해도 됩니다.
                다만 제작 방식, 원산지, 한정판 여부, 정품/보증, 정확한 소재 등급처럼 상품별 사실 확인이 필요한 정보는 사실처럼 단정하지 마세요.
                답변은 고객에게 말하듯 자연스럽게 작성하고, 기본적으로 2~5문장 안에서 끝내세요.
                사용자가 비교, 목록, 관리 순서를 요구한 경우에만 bullet을 사용하세요.
                부족한 정보가 있어도 "확인된 정보가 없습니다"라고 말하지 말고, "세부 사양까지 보긴 어렵지만", "정확한 제작 배경까지는 안내가 어렵지만"처럼 부드럽게 표현하세요.
                모르는 항목을 따로 나열하지 말고, 현재 상품 정보로 판단 가능한 답변을 먼저 완성하세요.
                사용자가 묻지 않은 관리법, 세척법, 보관법은 덧붙이지 마세요.
                데일리 활용, 스타일, 가격, 디자인 질문에는 착용/활용 맥락을 중심으로 답하고, 소재 관리는 꼭 필요한 짧은 주의 문장 1개까지만 허용합니다.
                구체적인 관리 방법은 사용자가 관리, 보관, 세척, 오염, 비/습기, 물 사용을 직접 물었을 때만 설명하세요.
                가격, 디자인, 소재, 스타일, 사용 상황에 관한 질문은 제공된 상품 정보를 근거로 체감 가치나 어울리는 사용 맥락까지 설명하세요.

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
        Product product = getProduct(productId);
        String productInfo = productPromptContextBuilder.buildProductContext(product);

        // 2줄 이내 요약 프롬프트 설정
        String prompt = """
                당신은 MCM 명품/패션 상품 용어를 짧게 설명하는 전문가입니다.
                아래 상품 정보와 소재 정보는 답변의 내부 참고 자료입니다.
                고객에게 "DB", "데이터베이스", "근거 자료", "확인된 정보" 같은 내부 표현을 말하지 마세요.
                선택된 키워드를 2줄 이내로 아주 간결하게 설명하세요.
                단순 사전 설명보다 이 상품에서는 어떤 의미로 볼 수 있는지 자연스럽게 설명하세요.
                제공된 상품 정보만으로 설명이 부족하면, MCM 제품군 안에서 일반적으로 통용되는 의미를 "보통", "일반적으로", "MCM의 이런 유형 제품은"처럼 표현해 보완해도 됩니다.
                제공되지 않은 상품별 구체 정보는 단정하지 말고, 현재 상품 정보로 판단 가능한 범위에서 답하세요.

                %s

                선택된 키워드: %s
                """.formatted(productInfo, keyword);
        String shortReply = llmClient.askFast(prompt);

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

        Product product = getProduct(productId);
        String productInfo = productPromptContextBuilder.buildProductContext(product);

        String history = buildHistoryContext(session.getId());
        String prompt = """
                당신은 MCM 명품/패션 제품 관리 전문가입니다.
                아래 상품 정보와 소재 관리 정보는 답변의 내부 참고 자료입니다.
                고객에게 "DB", "데이터베이스", "근거 자료", "확인된 정보" 같은 내부 표현을 말하지 마세요.
                careInfo, careSummary, cleaningMethod, storageMethod, avoidList, waterWarning, repairRecommendation을 우선 반영하세요.
                상품의 productType, color, clothsize, bagsize, styleTypes를 함께 고려해서 실제 사용자가 바로 따라할 수 있는 관리 팁으로 풀어주세요.
                제공된 상품 정보에 있는 사실은 자연스럽게 활용하고, 소재 특성에서 이어지는 관리 조언은 전문가 의견처럼 말해도 됩니다.
                제공된 상품 정보만으로 답변이 빈약하면, MCM의 같은 소재나 같은 유형 제품에서 일반적으로 권장되는 관리법을 "보통", "일반적으로"처럼 표현해 보완해도 됩니다.
                사용자의 질문 범위를 벗어나는 관리 팁은 덧붙이지 마세요.
                비, 습기, 물 관련 질문에는 waterWarning, storageMethod, avoidList를 중심으로 답하고, 세척제나 클리너 이야기는 사용자가 세척/오염 제거를 물었을 때만 언급하세요.
                다만 제공되지 않은 특수 가공, 정확한 손상 원인, 보증 가능 여부, 정품/수선 정책은 단정하지 마세요.
                답변은 고객에게 말하듯 자연스럽게 작성하고, 기본적으로 2~5문장 안에서 끝내세요.

                %s

                [대화 기록]
                %s

                사용자: %s
                """.formatted(productInfo, history, request.getMessage());

        String aiReply = llmClient.ask(prompt);
        chatRepository.save(new ChatMessage(aiReply, "ASSISTANT", session));

        return new ChatResponse(session.getId(), aiReply);
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 상품을 찾을 수 없습니다."));
    }
}
