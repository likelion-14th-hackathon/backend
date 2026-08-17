package com.example.likelion14th_hackathon.notification.service;

import com.example.likelion14th_hackathon.care.dto.WeatherInfo;
import com.example.likelion14th_hackathon.catalog.domain.Product;
import com.example.likelion14th_hackathon.catalog.service.ProductPromptContextBuilder;
import com.example.likelion14th_hackathon.common.llm.LlmClient;
import com.example.likelion14th_hackathon.mypage.domain.OwnedProduct;
import com.example.likelion14th_hackathon.notification.domain.NotificationTriggerType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationMessageGenerator {

    private final LlmClient llmClient;
    private final ProductPromptContextBuilder productPromptContextBuilder;

    public String createTitle(NotificationTriggerType triggerType) {
        if (triggerType == NotificationTriggerType.WEATHER_ALERT) {
            return "날씨 기반 케어 알림";
        }
        if (triggerType == NotificationTriggerType.CARE_CYCLE) {
            return "정기 케어 알림";
        }
        return "제품 케어 알림";
    }

    public String createWeatherAlertMessage(OwnedProduct ownedProduct, WeatherInfo weather) {
        String productName = getProductName(ownedProduct);
        String fallbackMessage = String.format("현재 %s, 습도 %d%%입니다. %s 관리에 주의해 주세요.",
                weather.weatherText(), weather.humidity(), productName);

        String prompt = """
                당신은 MCM 명품/패션 제품의 케어 알림 문구를 작성하는 전문가입니다.
                사용자가 푸시 알림으로 바로 이해할 수 있도록 한국어 한 문장으로 작성하세요.
                현재 날씨와 상품/소재별 관리 정보를 반영하세요.
                과장하지 말고, 확인된 관리 정보가 있으면 우선 사용하세요.
                답변에는 알림 문구만 포함하세요.

                [현재 날씨]
                weather: %s
                temperature: %.1f°C
                humidity: %d%%

                %s
                """.formatted(
                weather.weatherText(),
                weather.temperature(),
                weather.humidity(),
                productPromptContextBuilder.buildOwnedProductContext(ownedProduct.getId(), ownedProduct)
        );

        return askLlmOrFallback(prompt, fallbackMessage);
    }

    public String createCareCycleMessage(OwnedProduct ownedProduct) {
        String productName = getProductName(ownedProduct);
        String fallbackMessage = String.format("%s의 정기 케어 주기가 도래했습니다. 소재에 맞는 관리 상태를 확인해 주세요.", productName);
        String prompt = """
                당신은 MCM 명품/패션 제품의 정기 케어 알림 문구를 작성하는 전문가입니다.
                사용자가 푸시 알림으로 바로 이해할 수 있도록 한국어 한 문장으로 작성하세요.
                상품/소재별 관리 정보와 구매 시점을 반영하세요.
                과장하지 말고, 확인된 관리 정보가 있으면 우선 사용하세요.
                답변에는 알림 문구만 포함하세요.

                %s
                """.formatted(productPromptContextBuilder.buildOwnedProductContext(ownedProduct.getId(), ownedProduct));

        return askLlmOrFallback(prompt, fallbackMessage);
    }

    private String askLlmOrFallback(String prompt, String fallbackMessage) {
        try {
            String message = llmClient.ask(prompt);
            if (message == null || message.isBlank()) {
                return fallbackMessage;
            }
            return message.strip();
        } catch (RuntimeException exception) {
            log.warn("LLM 알림 문구 생성에 실패해 기본 알림 문구를 사용합니다.", exception);
            return fallbackMessage;
        }
    }

    private String getProductName(OwnedProduct ownedProduct) {
        Product product = ownedProduct.getProduct();
        if (product == null || product.getName() == null || product.getName().isBlank()) {
            return "등록 제품";
        }
        return product.getName();
    }
}
