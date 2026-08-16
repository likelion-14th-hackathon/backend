package com.example.likelion14th_hackathon.care.service;

import com.example.likelion14th_hackathon.catalog.domain.OwnedProduct;
import com.example.likelion14th_hackathon.catalog.repository.OwnedProductRepository;
import com.example.likelion14th_hackathon.catalog.service.ProductPromptContextBuilder;
import com.example.likelion14th_hackathon.care.dto.WeatherInfo;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CareService {

    private final SessionRepository sessionRepository;
    private final ChatRepository chatRepository;
    private final ChatSessionService chatSessionService;
    private final LlmClient llmClient;
    private final WeatherApiService weatherApiService;
    private final OwnedProductRepository ownedProductRepository;
    private final ProductPromptContextBuilder productPromptContextBuilder;

    private String buildHistoryContext(Long sessionId) {
        List<ChatMessage> history = chatRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        return history.stream()
                .map(m -> m.getRole() + ": " + m.getContent())
                .collect(Collectors.joining("\n"));
    }

    @Transactional
    public ChatResponse processCareChat(Long userProductId, ChatRequest request) {
        // 세션 조회 또는 생성 (새로고침/재접속 대응)
        ChatSession session = chatSessionService.getOrCreate(request.getSessionId());

        // 유저 메시지 저장 (순서: role, content, session)
        chatRepository.save(new ChatMessage(request.getMessage(), "USER", session));

        // LLM 프롬프트 생성 및 답변 도출
        OwnedProduct ownedProduct = ownedProductRepository.findById(userProductId).orElse(null);
        String productContext = productPromptContextBuilder.buildOwnedProductContext(userProductId, ownedProduct);
        String history = buildHistoryContext(session.getId());
        String prompt = """
                당신은 사용자가 보유한 MCM 명품/패션 제품의 AI CARE 상담사입니다.
                아래 보유 상품, 상품, 소재 정보를 기준으로 관리 방법을 안내하세요.
                careInfo와 소재 테이블의 careSummary, cleaningMethod, storageMethod, avoidList, waterWarning, repairRecommendation을 우선 반영하세요.
                purchasedAt과 status가 있으면 사용 기간과 현재 상태를 고려하세요.
                확인되지 않은 정보는 추측하지 말고 "확인된 정보가 없습니다"라고 말하세요.

                %s

                [대화 기록]
                %s

                사용자: %s
                """.formatted(productContext, history, request.getMessage());
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
        String triggerType = request.getTriggerType();
        OwnedProduct ownedProduct = ownedProductRepository.findById(userProductId).orElse(null);
        String productContext = productPromptContextBuilder.buildOwnedProductContext(userProductId, ownedProduct);
        String triggerContext;

        if ("WEATHER_ALERT".equals(triggerType)) {
            // 서울 기준 좌표 (37.5665, 126.9780) 호출 (필요시 request에서 위경도 파라미터를 받아와 넘길 수도 있습니다)
            WeatherInfo weather = weatherApiService.getWeather(37.5665, 126.9780);

            // 실시간 날씨 정보를 동적으로 반영한 메시지 생성
            triggerContext = String.format(
                    "트리거: WEATHER_ALERT%n현재 날씨: %s, 기온: %.1f°C, 습도: %d%%",
                    weather.weatherText(), weather.temperature(), weather.humidity()
            );
        } else if ("CARE_CYCLE".equals(triggerType)) {
            triggerContext = "트리거: CARE_CYCLE";
        } else {
            triggerContext = "트리거: GENERAL_CARE_INIT";
        }

        String prompt = """
                당신은 사용자가 보유한 MCM 명품/패션 제품의 AI CARE 상담사입니다.
                푸시 알림 또는 케어 화면 초기 진입에 사용할 첫 안내 문구를 한국어로 작성하세요.
                아래 보유 상품, 상품, 소재 정보와 트리거 정보를 반영하세요.
                careInfo와 소재별 관리 정보를 우선 사용하고, 정보가 없으면 일반적인 환영 문구로 짧게 안내하세요.
                답변은 2문장 이내로 작성하세요.

                %s

                [진입 트리거]
                %s
                """.formatted(productContext, triggerContext);
        String initialMessage = llmClient.ask(prompt);

        // 초기 환영 인사 메시지 저장
        chatRepository.save(new ChatMessage(initialMessage, "ASSISTANT", session));

        return ChatResponse.ofInit(session.getId(), initialMessage);
    }
}
