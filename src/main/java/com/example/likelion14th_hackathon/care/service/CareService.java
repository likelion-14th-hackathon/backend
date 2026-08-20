package com.example.likelion14th_hackathon.care.service;

import com.example.likelion14th_hackathon.catalog.service.ProductPromptContextBuilder;
import com.example.likelion14th_hackathon.care.dto.WeatherInfo;
import com.example.likelion14th_hackathon.common.api.ResourceNotFoundException;
import com.example.likelion14th_hackathon.common.api.UnauthorizedException;
import com.example.likelion14th_hackathon.common.chat.domain.ChatMessage;
import com.example.likelion14th_hackathon.common.chat.domain.ChatSession;
import com.example.likelion14th_hackathon.common.chat.dto.ChatRequest;
import com.example.likelion14th_hackathon.common.chat.dto.ChatResponse;
import com.example.likelion14th_hackathon.common.chat.repository.ChatRepository;
import com.example.likelion14th_hackathon.common.chat.repository.SessionRepository;
import com.example.likelion14th_hackathon.common.chat.service.ChatSessionService;
import com.example.likelion14th_hackathon.common.llm.LlmClient;
import com.example.likelion14th_hackathon.mypage.domain.OwnedProduct;
import com.example.likelion14th_hackathon.mypage.repository.OwnedProductRepository;
import com.example.likelion14th_hackathon.notification.domain.Notification;
import com.example.likelion14th_hackathon.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CareService {

    private static final double SEOUL_LATITUDE = 37.5665;
    private static final double SEOUL_LONGITUDE = 126.9780;

    private final SessionRepository sessionRepository;
    private final ChatRepository chatRepository;
    private final ChatSessionService chatSessionService;
    private final LlmClient llmClient;
    private final WeatherApiService weatherApiService;
    private final OwnedProductRepository ownedProductRepository;
    private final ProductPromptContextBuilder productPromptContextBuilder;
    private final NotificationRepository notificationRepository;

    private String buildHistoryContext(Long sessionId) {
        List<ChatMessage> history = chatRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        return history.stream()
                .map(m -> m.getRole() + ": " + m.getContent())
                .collect(Collectors.joining("\n"));
    }

    @Transactional
    public ChatResponse processCareChat(Long memberId, Long userProductId, ChatRequest request) {
        // 세션 조회 또는 생성 (새로고침/재접속 대응)
        ChatSession session = chatSessionService.getOrCreate(request.getSessionId());

        // 유저 메시지 저장 (순서: role, content, session)
        chatRepository.save(new ChatMessage(request.getMessage(), "USER", session));

        // LLM 프롬프트 생성 및 답변 도출
        OwnedProduct ownedProduct = getOwnedProductForMember(memberId, userProductId);
        String productContext = productPromptContextBuilder.buildOwnedProductContext(userProductId, ownedProduct);
        String weatherContext = buildWeatherContext();
        String history = buildHistoryContext(session.getId());
        String prompt = """
                당신은 사용자가 보유한 MCM 명품/패션 제품의 AI CARE 상담사입니다.
                아래 보유 상품, 상품, 소재 정보는 답변의 내부 참고 자료입니다.
                현재 날씨 정보가 제공된 경우, 습도/비/눈/뇌우/기온 관련 질문에는 그 값을 활용해서 답하세요.
                고객에게 "DB", "데이터베이스", "근거 자료", "확인된 정보" 같은 내부 표현을 말하지 마세요.
                careInfo와 소재 테이블의 careSummary, cleaningMethod, storageMethod, avoidList, waterWarning, repairRecommendation을 우선 반영하세요.
                purchasedAt, status, productType, color, size, styleTypes까지 종합해서 사용자가 바로 실행할 수 있는 관리 조언으로 풀어주세요.
                제공된 상품 정보에 있는 사실은 자연스럽게 활용하고, 소재 특성에서 이어지는 조언은 전문가 의견처럼 말해도 됩니다.
                제공된 상품 정보만으로 답변이 빈약하면, MCM의 같은 소재나 같은 유형 제품에서 일반적으로 권장되는 관리법을 "보통", "일반적으로"처럼 표현해 보완해도 됩니다.
                사용자의 질문 범위를 벗어나는 관리 팁은 덧붙이지 마세요.
                비, 습기, 물 관련 질문에는 waterWarning, storageMethod, avoidList를 중심으로 답하고, 세척제나 클리너 이야기는 사용자가 세척/오염 제거를 물었을 때만 언급하세요.
                다만 제공되지 않은 특수 가공, 정확한 손상 원인, 보증 가능 여부, 정품/수선 정책은 단정하지 마세요.
                부족한 정보가 있어도 "확인된 정보가 없습니다"라고 말하지 말고, 현재 상품 정보로 판단 가능한 관리 방법을 먼저 안내하세요.
                답변은 고객에게 말하듯 자연스럽게 작성하고, 기본적으로 2~5문장 안에서 끝내세요.

                %s

                %s

                [대화 기록]
                %s

                사용자: %s
                """.formatted(productContext, weatherContext, history, request.getMessage());
        String reply = llmClient.ask(prompt);

        // AI 답변 메시지 저장
        chatRepository.save(new ChatMessage(reply, "ASSISTANT", session));

        // 응답 반환
        return new ChatResponse(session.getId(), reply);
    }

    @Transactional
    public ChatResponse initCareChat(Long memberId, Long userProductId, ChatRequest request) {
        // 푸시 알림 클릭 진입 시 새로운 세션 발급
        ChatSession session = sessionRepository.save(new ChatSession());

        // 트리거 타입에 따른 맞춤형 환영 인사 생성
        String triggerType = request.getTriggerType();
        OwnedProduct ownedProduct = getOwnedProductForMember(memberId, userProductId);
        Notification notification = getNotificationForInit(memberId, userProductId, request.getNotificationId());
        if (notification != null) {
            triggerType = notification.getTriggerType().name();
            notification.markAsRead();
        }
        String productContext = productPromptContextBuilder.buildOwnedProductContext(userProductId, ownedProduct);
        String triggerContext;

        if ("WEATHER_ALERT".equals(triggerType)) {
            // 서울 기준 좌표 (37.5665, 126.9780) 호출 (필요시 request에서 위경도 파라미터를 받아와 넘길 수도 있습니다)
            WeatherInfo weather = weatherApiService.getWeather(SEOUL_LATITUDE, SEOUL_LONGITUDE);

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
                아래 보유 상품, 상품, 소재 정보와 트리거 정보는 답변의 내부 참고 자료입니다.
                고객에게 "DB", "데이터베이스", "근거 자료", "확인된 정보" 같은 내부 표현을 말하지 마세요.
                careInfo와 소재별 관리 정보를 우선 사용하되, 상품의 스타일과 사용 맥락까지 고려해 자연스러운 상담 시작 문장으로 작성하세요.
                제공된 상품 정보만으로 문구가 빈약하면, MCM의 같은 소재나 같은 유형 제품에서 일반적으로 권장되는 케어 포인트를 자연스럽게 보완해도 됩니다.
                제공되지 않은 구체 사실은 단정하지 말고, 현재 상품 정보로 판단 가능한 케어 포인트를 먼저 말하세요.
                트리거가 CARE_CYCLE이면 정기 케어 주기가 돌아왔다는 점을 자연스럽게 언급하고, 오늘 확인하면 좋은 관리 행동을 이어서 안내하세요.
                트리거가 WEATHER_ALERT이면 현재 날씨 때문에 주의가 필요하다는 점을 자연스럽게 언급하고, 날씨에 맞는 관리 행동을 이어서 안내하세요.
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

    private String buildWeatherContext() {
        try {
            WeatherInfo weather = weatherApiService.getWeather(SEOUL_LATITUDE, SEOUL_LONGITUDE);
            return """
                    [현재 날씨]
                    날씨: %s
                    기온: %.1f°C
                    습도: %d%%
                    """.formatted(weather.weatherText(), weather.temperature(), weather.humidity());
        } catch (RuntimeException exception) {
            return "";
        }
    }

    private Notification getNotificationForInit(Long memberId, Long userProductId, Long notificationId) {
        if (notificationId == null) {
            return null;
        }

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("알림을 찾을 수 없습니다."));

        if (!notification.getMemberId().equals(memberId)) {
            throw new UnauthorizedException("해당 알림에 접근할 권한이 없습니다.");
        }

        if (!notification.getUserProductId().equals(userProductId)) {
            throw new UnauthorizedException("알림과 등록 제품 정보가 일치하지 않습니다.");
        }

        return notification;
    }

    private OwnedProduct getOwnedProductForMember(Long memberId, Long userProductId) {
        OwnedProduct ownedProduct = ownedProductRepository.findById(userProductId)
                .orElseThrow(() -> new ResourceNotFoundException("등록 제품을 찾을 수 없습니다."));

        if (!ownedProduct.getMemberId().equals(memberId)) {
            throw new UnauthorizedException("해당 등록 제품에 접근할 권한이 없습니다.");
        }

        return ownedProduct;
    }
}
