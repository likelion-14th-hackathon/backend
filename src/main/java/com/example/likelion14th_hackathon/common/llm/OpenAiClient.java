package com.example.likelion14th_hackathon.common.llm;

import com.example.likelion14th_hackathon.common.exception.ExternalApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "llm.provider", havingValue = "openai")
public class OpenAiClient implements LlmClient {

    private final RestClient restClient;
    private final String apiKey;
    private final String fastModel;
    private final String reasoningModel;

    public OpenAiClient(
            RestClient.Builder restClientBuilder,
            @Value("${llm.openai.api-key:}") String apiKey,
            @Value("${llm.openai.fast-model:gpt-4o-mini}") String fastModel,
            @Value("${llm.openai.reasoning-model:gpt-5.5}") String reasoningModel
    ) {
        this.restClient = restClientBuilder
                .baseUrl("https://api.openai.com")
                .build();
        this.apiKey = apiKey;
        this.fastModel = fastModel;
        this.reasoningModel = reasoningModel;
    }

    @Override
    public String askFast(String prompt) {
        return askWithModel(fastModel, prompt);
    }

    @Override
    public String askReasoning(String prompt) {
        return askWithModel(reasoningModel, prompt);
    }

    @Override
    public String askWithImages(String prompt, List<String> imageUrls) {
        // 이미지 분석은 분류 성격의 작업이므로 fastModel(gpt-4o-mini)을 사용한다.
        return askWithModelAndImages(fastModel, prompt, imageUrls);
    }

    private String askWithModel(String model, String prompt) {
        validateApiKey();

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                )
        );

        return call(requestBody, model);
    }

    private String askWithModelAndImages(String model, String prompt, List<String> imageUrls) {
        validateApiKey();

        if (imageUrls == null || imageUrls.isEmpty()) {
            throw new ExternalApiException("이미지 URL이 비어 있습니다.");
        }

        // content 배열: 지시문을 먼저, 그다음 이미지들을 넣는다.
        List<Map<String, Object>> content = new ArrayList<>();
        content.add(Map.of("type", "text", "text", prompt));
        for (String imageUrl : imageUrls) {
            content.add(Map.of(
                    "type", "image_url",
                    "image_url", Map.of("url", imageUrl)
            ));
        }

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", content
                        )
                )
        );

        return call(requestBody, model);
    }

    private void validateApiKey() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ExternalApiException("OpenAI API Key가 설정되지 않았습니다. 프로젝트 루트의 .env 파일에 OPENAI_API_KEY를 입력해주세요.");
        }
    }

    private String call(Map<String, Object> requestBody, String model) {
        try {
            OpenAiResponse response = restClient.post()
                    .uri("/v1/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .body(requestBody)
                    .retrieve()
                    .body(OpenAiResponse.class);

            if (response == null || response.choices() == null || response.choices().isEmpty()) {
                throw new ExternalApiException("OpenAI 응답이 비어 있습니다.");
            }
            return response.choices().get(0).message().content();
        } catch (RestClientException e) {
            throw new ExternalApiException("OpenAI 호출에 실패했습니다. 모델명 또는 API Key를 확인해주세요. model=" + model);
        }
    }

    private record OpenAiResponse(List<OpenAiChoice> choices) {
    }

    private record OpenAiChoice(OpenAiMessage message) {
    }

    private record OpenAiMessage(String role, String content) {
    }
}