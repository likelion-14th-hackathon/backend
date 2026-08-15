package com.example.likelion14th_hackathon.member.presentation.dto;

import com.example.likelion14th_hackathon.member.domain.Member;

public record MemberResponse(
        Long memberId,
        String email,
        String nickname
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getEmail(), member.getNickname());
    }
}
