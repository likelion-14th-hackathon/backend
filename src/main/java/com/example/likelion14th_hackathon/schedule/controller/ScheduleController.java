package com.example.likelion14th_hackathon.schedule.controller;

import com.example.likelion14th_hackathon.common.api.ApiResponse;
import com.example.likelion14th_hackathon.common.security.JwtTokenProvider;
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
    private final JwtTokenProvider jwtTokenProvider;   // 추가

    @PostMapping
    public ResponseEntity<ApiResponse<ScheduleResponse>> create(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ScheduleRequest request
    ) {
        Long memberId = jwtTokenProvider.extractMemberIdFromAuthorization(authorizationHeader);
        ScheduleResponse result = scheduleService.createSchedule(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(result, "일정을 등록했습니다."));
    }

    @GetMapping(params = "date")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getByDate(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        Long memberId = jwtTokenProvider.extractMemberIdFromAuthorization(authorizationHeader);
        List<ScheduleResponse> result = scheduleService.getByDate(memberId, date);
        return ResponseEntity.ok(ApiResponse.success(result, "일정을 조회했습니다."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getThisMonth(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        Long memberId = jwtTokenProvider.extractMemberIdFromAuthorization(authorizationHeader);
        List<ScheduleResponse> result = scheduleService.getThisMonth(memberId);
        return ResponseEntity.ok(ApiResponse.success(result, "이번 달 일정을 조회했습니다."));
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<ApiResponse<ScheduleResponse>> update(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long scheduleId,
            @RequestBody ScheduleRequest request
    ) {
        Long memberId = jwtTokenProvider.extractMemberIdFromAuthorization(authorizationHeader);
        ScheduleResponse result = scheduleService.updateSchedule(memberId, scheduleId, request);
        return ResponseEntity.ok(ApiResponse.success(result, "일정을 수정했습니다."));
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long scheduleId
    ) {
        Long memberId = jwtTokenProvider.extractMemberIdFromAuthorization(authorizationHeader);
        scheduleService.deleteSchedule(memberId, scheduleId);
        return ResponseEntity.ok(ApiResponse.success(null, "일정을 삭제했습니다."));
    }
}