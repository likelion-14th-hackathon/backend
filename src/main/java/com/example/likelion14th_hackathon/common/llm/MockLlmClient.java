package com.example.likelion14th_hackathon.common.llm;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "llm.provider", havingValue = "mock", matchIfMissing = true)
public class MockLlmClient implements LlmClient {

    @Override
    public String askFast(String prompt) {
        return answer(prompt);
    }

    @Override
    public String askReasoning(String prompt) {
        return answer(prompt);
    }

    private String answer(String prompt) {
        if (prompt.contains("관리법")) {
            return "부드러운 마른 천으로 닦고, 직사광선과 습기를 피해 보관하세요. 오염이 생기면 강하게 문지르지 말고 전문 케어를 권장합니다.";
        }

        if (prompt.contains("추천")) {
            return "일정 분위기에 맞춰 이미 구매한 제품은 단정한 포인트 아이템으로 활용하고, 구매하지 않은 제품은 비즈니스 캐주얼 계열로 함께 추천합니다.";
        }

        return "상품 정보를 바탕으로 답변드릴게요. 해당 제품은 데일리로 활용하기 좋은 제품입니다.";
    }
}

