package com.example.likelion14th_hackathon.common.llm;

public interface LlmClient {
    String ask(String prompt);

    String askFast(String prompt);
}
