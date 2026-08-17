package com.example.likelion14th_hackathon.common.llm;

public interface LlmClient {

    // 기본 호출은 추천/생성처럼 품질이 더 중요한 작업을 기준으로 한다.
    default String ask(String prompt) {
        return askReasoning(prompt);
    }

    // 롱클릭 단어 설명처럼 사용자가 즉시 답변을 기대하는 기능에서 사용한다.
    String askFast(String prompt);

    // 추천/문장 생성/추론처럼 품질이 더 중요한 기능에서 사용한다.
    String askReasoning(String prompt);
}
