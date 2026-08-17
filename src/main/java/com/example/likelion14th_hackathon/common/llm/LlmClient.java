package com.example.likelion14th_hackathon.common.llm;

public interface LlmClient {

    default String ask(String prompt) {
        return askReasoning(prompt);
    }

    String askFast(String prompt);

    String askReasoning(String prompt);
}
