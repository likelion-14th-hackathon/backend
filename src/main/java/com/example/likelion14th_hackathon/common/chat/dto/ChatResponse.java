package com.example.likelion14th_hackathon.common.chat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // 값이 null인 필드는 응답 JSON에서 제외
public class ChatResponse {
    private Long sessionId;
    private String reply;          // 일반 문의, 관리법, AI CARE 일반 채팅용
    private String keyword;        // 키워드 챗봇용
    private String shortReply;     // 키워드 챗봇용
    private String initialMessage; // 푸시 알림 연동 초기 진입용

    // 1. 일반 문의, 관리법, AI CARE 일반 채팅용 생성자
    public ChatResponse(Long sessionId, String reply) {
        this.sessionId = sessionId;
        this.reply = reply;
    }

    // 2. 단어 롱클릭 키워드 챗봇용 생성자
    public ChatResponse(Long sessionId, String keyword, String shortReply) {
        this.sessionId = sessionId;
        this.keyword = keyword;
        this.shortReply = shortReply;
    }

    // 3. 푸시 알림 초기 진입(init)용 생성자
    public static ChatResponse ofInit(Long sessionId, String initialMessage) {
        ChatResponse response = new ChatResponse(sessionId, null, null, null, initialMessage);
        return response;
    }
}
