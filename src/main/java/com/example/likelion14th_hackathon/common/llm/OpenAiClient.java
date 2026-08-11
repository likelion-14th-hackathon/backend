package com.example.likelion14th_hackathon.common.llm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "llm.provider", havingValue = "openai", matchIfMissing = true)
public class OpenAiClient implements LlmClient {
//    OpenAiClient는 LlmClient 인터페이스를 구현한다.
//그래서 ask(String prompt) 메서드를 반드시 가져야 한다.
//서비스에서는 LlmClient 타입으로 공통 사용이 가능하다.
// 이 구조 덕분에 OpenAI → Qwen → Claude로 바꿔도 서비스 코드를 안 바꿔도 된다.

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public OpenAiClient(
            RestClient.Builder restClientBuilder,
            @Value("${llm.openai.api-key:}") String apiKey,
            @Value("${llm.openai.model:gpt-4o-mini}") String model
    ) {
        this.restClient = restClientBuilder
                .baseUrl("https://api.openai.com")
                .build();
        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    public String ask(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                )
        );

        OpenAiResponse response = restClient.post()
                .uri("/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .body(requestBody)
                .retrieve()
                .body(OpenAiResponse.class);

        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            return "";
        }

        return response.choices().get(0).message().content();
    }

    private record OpenAiResponse(List<OpenAiChoice> choices) {
    }

    private record OpenAiChoice(OpenAiMessage message) {
    }

    private record OpenAiMessage(String role, String content) {
    }
}
