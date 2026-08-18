package com.example.likelion14th_hackathon.schedule.service;

import com.example.likelion14th_hackathon.member.domain.Member;
import com.example.likelion14th_hackathon.member.repository.MemberRepository;
import com.example.likelion14th_hackathon.schedule.dto.ScheduleRequest;
import com.example.likelion14th_hackathon.schedule.dto.ScheduleResponse;
import com.example.likelion14th_hackathon.schedule.entity.Schedule;
import com.example.likelion14th_hackathon.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;

    // 1. 일정 등록
    @Transactional
    public ScheduleResponse createSchedule(Long memberId, ScheduleRequest request) {
        Member member = findMemberOrThrow(memberId);
        Schedule schedule = new Schedule(
                member, request.getDate(), request.getCategory(),
                request.getTitle(), request.getLocation()
        );
        scheduleRepository.save(schedule);
        return new ScheduleResponse(schedule);
    }

    // 2. 특정 날짜 일정 조회
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getByDate(Long memberId, LocalDate date) {
        Member member = findMemberOrThrow(memberId);
        return scheduleRepository.findByMemberAndDate(member, date).stream()
                .map(ScheduleResponse::new)
                .toList();
    }

    // 3. 이번 달 일정 조회
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getThisMonth(Long memberId) {
        Member member = findMemberOrThrow(memberId);

        LocalDate today = LocalDate.now();
        LocalDate start = today.withDayOfMonth(1);                       // 이번 달 1일
        LocalDate end = today.withDayOfMonth(today.lengthOfMonth());     // 이번 달 말일

        return scheduleRepository
                .findByMemberAndDateBetweenOrderByDateAsc(member, start, end).stream()
                .map(ScheduleResponse::new)
                .toList();
    }

    // 4. 일정 수정
    @Transactional
    public ScheduleResponse updateSchedule(Long memberId, Long scheduleId, ScheduleRequest request) {
        Schedule schedule = findScheduleOrThrow(scheduleId);
        verifyOwner(schedule, memberId);
        schedule.update(request.getDate(), request.getCategory(),
                request.getTitle(), request.getLocation());
        return new ScheduleResponse(schedule);   // 트랜잭션 종료 시 변경 자동 반영
    }

    // 5. 일정 삭제
    @Transactional
    public void deleteSchedule(Long memberId, Long scheduleId) {
        Schedule schedule = findScheduleOrThrow(scheduleId);
        verifyOwner(schedule, memberId);
        scheduleRepository.delete(schedule);
    }

    // ===== 내부 헬퍼 =====
    private Member findMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
    }

    private Schedule findScheduleOrThrow(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));
    }

    private void verifyOwner(Schedule schedule, Long memberId) {
        if (!schedule.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("해당 일정에 대한 권한이 없습니다.");
        }
    }
}
