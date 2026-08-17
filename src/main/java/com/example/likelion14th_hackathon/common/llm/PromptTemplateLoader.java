package com.example.likelion14th_hackathon.common.llm;

import org.springframework.stereotype.Component;

@Component
public class PromptTemplateLoader {

    public String productQuestionPrompt(
            String productName,
            Integer price,
            String productType,
            String color,
            Long materialId,
            String materialName,
            String styleCategory,
            String description,
            String question
    ) {
        return """
                너는 MCM 브랜드 웹사이트의 친절한 AI 직원이야.
                아래 상품 정보만 바탕으로 사용자의 질문에 답변해줘.
                상품 정보에 없는 내용은 추측하지 말고 확인하기 어렵다고 말해.

                [상품 정보]
                제품명: %s
                제품 유형: %s
                가격: %d원
                색상: %s
                소재 ID: %d
                소재명: %s
                패션 스타일: %s
                제품 설명: %s

                [사용자 질문]
                %s
                """.formatted(productName, productType, price, color, materialId, materialName, styleCategory, description, question);
    }

    public String careGuidePrompt(
            Long productId,
            String productName,
            Long materialId,
            String materialName,
            String careSummary,
            String cleaningMethod,
            String storageMethod,
            String avoidList,
            String waterWarning,
            String repairRecommendation
    ) {
        return """
                다음 제품과 소재 정보를 바탕으로 고객에게 보여줄 관리법을 짧고 쉽게 정리해줘.
                소재 테이블에 있는 정보를 우선 사용하고, 없는 내용은 추측하지 마.

                제품 ID: %d
                제품명: %s
                소재 ID: %d
                소재명: %s

                [소재 관리 정보]
                요약: %s
                세척 방법: %s
                보관 방법: %s
                피해야 할 것: %s
                물 주의사항: %s
                수선 권장사항: %s
                """.formatted(
                productId,
                productName,
                materialId,
                materialName,
                careSummary,
                cleaningMethod,
                storageMethod,
                avoidList,
                waterWarning,
                repairRecommendation
        );
    }

    // 추가: 사진 기반 사용자 스타일 분석
    public String styleAnalysisPrompt() {
        return """
                다음 사진들은 한 사용자가 업로드한 옷차림 사진입니다.
                사진들을 종합해서 이 사용자의 전반적인 패션 스타일을 판단해 주세요.

                반드시 아래 8개 중에서만 선택하세요:
                MINIMAL, CASUAL, STREET, CLASSIC, VINTAGE, SPORTY, ROMANTIC, CHIC

                가장 잘 맞는 순서대로 최대 3개까지 고르세요.
                설명이나 다른 텍스트 없이, 아래 JSON 형식으로만 답하세요:
                {"styles": ["STREET", "CASUAL"]}
                """;
    }
}