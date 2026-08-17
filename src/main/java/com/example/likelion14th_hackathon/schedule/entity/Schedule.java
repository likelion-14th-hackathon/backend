package com.example.likelion14th_hackathon.schedule.entity;

import com.example.likelion14th_hackathon.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleCategory category;

    @Column(nullable = false)
    private String title;

    private String location;   // 선택

    protected Schedule() {}

    public Schedule(Member member, LocalDate date, ScheduleCategory category, String title, String location) {
        this.member = member;
        this.date = date;
        this.category = category;
        this.title = title;
        this.location = location;
    }

    // 수정용 (setter 대신)
    public void update(LocalDate date, ScheduleCategory category, String title, String location) {
        this.date = date;
        this.category = category;
        this.title = title;
        this.location = location;
    }
}
