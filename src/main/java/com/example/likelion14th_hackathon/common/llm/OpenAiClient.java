package com.example.likelion14th_hackathon.common.llm;

import com.example.likelion14th_hackathon.common.exception.ExternalApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

@Component
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

    private String askWithModel(String model, String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ExternalApiException("OpenAI API Key가 설정되지 않았습니다. 프로젝트 루트의 .env 파일에 OPENAI_API_KEY를 입력해주세요.");
        }

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                )
        );

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
