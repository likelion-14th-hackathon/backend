package com.example.likelion14th_hackathon.member.presentation.dto;

import com.example.likelion14th_hackathon.member.domain.Member;

public record AuthResponse(
        Long memberId,
        String email,
        String nickname,
        String accessToken,
        String tokenType,
        long expiresInSeconds
) {
    public static AuthResponse from(Member member, String accessToken, long expiresInSeconds) {
        return new AuthResponse(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                accessToken,
                "Bearer",
                expiresInSeconds
        );
    }
}
