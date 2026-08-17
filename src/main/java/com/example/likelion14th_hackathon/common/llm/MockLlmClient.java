package com.example.likelion14th_hackathon.common.llm;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

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


    /* .env에서 LLM_PROVIDER=mock 으로 바꾸면
        → 사진 업로드 시 스타일이 항상 [CASUAL, MINIMAL]로 저장됨
        → 추천 로직(TPO 필터 + 정렬 + 보유/미보유)이 정상 동작
        → AI 비용 0원
     */
    @Override
    public String askWithImages(String prompt, List<String> imageUrls) {
        // 스타일 분석은 JSON 형식 응답을 기대하므로 형식에 맞춰 반환한다.
        if (prompt.contains("패션 스타일")) {
            return "{\"styles\": [\"CASUAL\", \"MINIMAL\"]}";
        }
        return "이미지를 확인했습니다. 전반적으로 무난하고 활용도 높은 스타일로 보입니다.";
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