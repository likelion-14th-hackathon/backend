package com.example.likelion14th_hackathon.schedule.repository;

import com.example.likelion14th_hackathon.member.entity.Member;
import com.example.likelion14th_hackathon.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    // 특정 회원의 특정 날짜 일정 (하루 여러 개 가능)
    List<Schedule> findByMemberAndDate(Member member, LocalDate date);

    // 특정 회원의 날짜 범위 일정 (이번 달 조회용), 날짜 오름차순
    List<Schedule> findByMemberAndDateBetweenOrderByDateAsc(Member member, LocalDate start, LocalDate end);
}
