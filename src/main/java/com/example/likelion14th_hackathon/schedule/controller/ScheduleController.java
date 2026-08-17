package com.example.likelion14th_hackathon.schedule.controller;

import com.example.likelion14th_hackathon.common.api.ApiResponse;
import com.example.likelion14th_hackathon.schedule.dto.ScheduleRequest;
import com.example.likelion14th_hackathon.schedule.dto.ScheduleResponse;
import com.example.likelion14th_hackathon.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/mypage/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 1. 일정 등록
    @PostMapping
    public ResponseEntity<ApiResponse<ScheduleResponse>> createSchedule(
            @RequestHeader("X-User-Id") Long memberId,
            @RequestBody ScheduleRequest request
    ) {
        ScheduleResponse result = scheduleService.createSchedule(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.created(result, "일정을 등록했습니다.")
        );
    }

    // 2. 특정 날짜 일정 조회 (특정 날짜에 여러개 일정이 있을 수 있음)
    @GetMapping(params = "date")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getByDate(
            @RequestHeader("X-User-Id") Long memberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<ScheduleResponse> result = scheduleService.getByDate(memberId, date);
        return ResponseEntity.ok(
                ApiResponse.success(result, "일정을 조회했습니다.")
        );
    }

    // 3. 이번 달 일정 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getThisMonth(
            @RequestHeader("X-User-Id") Long memberId
    ) {
        List<ScheduleResponse> result = scheduleService.getThisMonth(memberId);
        return ResponseEntity.ok(
                ApiResponse.success(result, "이번 달 일정을 조회했습니다.")
        );
    }

    // 4. 일정 수정
    @PutMapping("/{scheduleId}")
    public ResponseEntity<ApiResponse<ScheduleResponse>> updateSchedule(
            @RequestHeader("X-User-Id") Long memberId,
            @PathVariable Long scheduleId,
            @RequestBody ScheduleRequest request
    ) {
        ScheduleResponse result = scheduleService.updateSchedule(memberId, scheduleId, request);
        return ResponseEntity.ok(
                ApiResponse.success(result, "일정을 수정했습니다.")
        );
    }

    // 5. 일정 삭제
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(
            @RequestHeader("X-User-Id") Long memberId,
            @PathVariable Long scheduleId
    ) {
        scheduleService.deleteSchedule(memberId, scheduleId);
        return ResponseEntity.ok(
                ApiResponse.success(null, "일정을 삭제했습니다.")
        );
    }
}