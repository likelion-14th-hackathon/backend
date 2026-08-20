package com.example.likelion14th_hackathon.member.presentation;

import com.example.likelion14th_hackathon.common.response.ApiResponse;
import com.example.likelion14th_hackathon.member.application.MemberService;
import com.example.likelion14th_hackathon.member.presentation.dto.MemberResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/members", "/api/members", "/auth/members"})
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/{memberId}")
    public ApiResponse<MemberResponse> getMember(@PathVariable Long memberId) {
        return ApiResponse.success(MemberResponse.from(memberService.getMember(memberId)));
    }
}
