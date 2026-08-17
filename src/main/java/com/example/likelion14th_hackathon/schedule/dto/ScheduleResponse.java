package com.example.likelion14th_hackathon.schedule.dto;

import com.example.likelion14th_hackathon.schedule.entity.Schedule;
import com.example.likelion14th_hackathon.schedule.entity.ScheduleCategory;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ScheduleResponse {
    private final Long scheduleId;
    private final LocalDate date;
    private final ScheduleCategory category;
    private final String title;
    private final String location;

    public ScheduleResponse(Schedule schedule) {
        this.scheduleId = schedule.getScheduleId();
        this.date = schedule.getDate();
        this.category = schedule.getCategory();
        this.title = schedule.getTitle();
        this.location = schedule.getLocation();
    }
}
