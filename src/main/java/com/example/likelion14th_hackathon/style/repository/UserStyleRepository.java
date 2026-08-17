package com.example.likelion14th_hackathon.style.repository;

import com.example.likelion14th_hackathon.member.entity.Member;   // TODO: 경로 확인
import com.example.likelion14th_hackathon.style.entity.UserStyle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserStyleRepository extends JpaRepository<UserStyle, Long> {

    List<UserStyle> findByMember(Member member);   // 이 회원의 스타일 목록

    void deleteByMember(Member member);            // AI 재분석 시 기존 것 제거
}