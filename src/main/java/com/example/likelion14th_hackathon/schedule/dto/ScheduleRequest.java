package com.example.likelion14th_hackathon.schedule.dto;

import com.example.likelion14th_hackathon.schedule.entity.ScheduleCategory;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ScheduleRequest {
    private LocalDate date;
    private ScheduleCategory category;
    private String title;
    private String location;
}
