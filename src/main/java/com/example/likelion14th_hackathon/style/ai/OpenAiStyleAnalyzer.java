package com.example.likelion14th_hackathon.style.ai;

import com.example.likelion14th_hackathon.common.llm.LlmClient;
import com.example.likelion14th_hackathon.common.llm.PromptTemplateLoader;
import com.example.likelion14th_hackathon.style.entity.StyleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiStyleAnalyzer {

    private final LlmClient llmClient;
    private final PromptTemplateLoader promptTemplateLoader;

    private static final int MAX_STYLES = 3;
    private static final int MAX_PHOTOS = 10;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 사진 URL들을 종합 분석해 사용자 스타일을 판별한다.
     * 실패 시 빈 목록을 반환하여 호출 측 흐름을 막지 않는다.
     */
    public List<StyleType> analyze(List<String> photoUrls) {
        if (photoUrls == null || photoUrls.isEmpty()) {
            log.info("분석할 사진이 없어 스타일 분석을 건너뜁니다.");
            return List.of();
        }

        List<String> targets = photoUrls.size() > MAX_PHOTOS
                ? photoUrls.subList(0, MAX_PHOTOS)
                : photoUrls;

        try {
            String response = llmClient.askWithImages(
                    promptTemplateLoader.styleAnalysisPrompt(),
                    targets
            );
            List<StyleType> styles = parseStyles(response);
            log.info("스타일 분석 완료: {} (사진 {}장)", styles, targets.size());
            return styles;

        } catch (Exception e) {
            log.error("스타일 분석 실패: {}", e.getMessage());
            return List.of();
        }
    }

    /** LLM 응답(JSON 문자열)에서 StyleType 목록 추출 */
    private List<StyleType> parseStyles(String content) {
        String cleaned = content
                .replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();

        JsonNode parsed = objectMapper.readTree(cleaned);
        JsonNode styleNodes = parsed.path("styles");

        List<StyleType> result = new ArrayList<>();
        for (JsonNode node : styleNodes) {
            if (result.size() >= MAX_STYLES) break;
            try {
                StyleType style = StyleType.valueOf(node.asText().trim().toUpperCase());
                if (!result.contains(style)) {
                    result.add(style);
                }
            } catch (IllegalArgumentException e) {
                log.warn("알 수 없는 스타일 값 무시: {}", node.asText());
            }
        }
        return result;
    }
}