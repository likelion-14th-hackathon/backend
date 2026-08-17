package com.example.likelion14th_hackathon.member.application;

import com.example.likelion14th_hackathon.common.security.JwtTokenProvider;
import com.example.likelion14th_hackathon.member.domain.Member;
import com.example.likelion14th_hackathon.member.presentation.dto.AuthResponse;
import com.example.likelion14th_hackathon.member.presentation.dto.LoginRequest;
import com.example.likelion14th_hackathon.member.presentation.dto.SignupRequest;
import com.example.likelion14th_hackathon.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(
            MemberRepository memberRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        Member member = memberRepository.save(new Member(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.nickname()
        ));

        String accessToken = jwtTokenProvider.createAccessToken(member);
        return AuthResponse.from(member, accessToken, jwtTokenProvider.getExpirationSeconds());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(member);
        return AuthResponse.from(member, accessToken, jwtTokenProvider.getExpirationSeconds());
    }
}
