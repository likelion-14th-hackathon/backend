package com.example.likelion14th_hackathon.notification.controller;

import com.example.likelion14th_hackathon.common.api.ApiResponse;
import com.example.likelion14th_hackathon.notification.dto.NotificationCreateRequest;
import com.example.likelion14th_hackathon.notification.dto.NotificationResponse;
import com.example.likelion14th_hackathon.notification.dto.TestNotificationResponse;
import com.example.likelion14th_hackathon.notification.service.NotificationNotFoundException;
import com.example.likelion14th_hackathon.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotifications() {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getNotifications(), "성공"));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(@PathVariable Long notificationId) {
        try {
            return ResponseEntity.ok(ApiResponse.success(notificationService.markAsRead(notificationId), "알림을 읽음 처리했습니다."));
        } catch (NotificationNotFoundException exception) {
            return ResponseEntity.status(404).body(ApiResponse.failure(404, exception.getMessage()));
        }
    }

    @PostMapping("/test-trigger")
    public ResponseEntity<ApiResponse<TestNotificationResponse>> createTestNotification(
            @Valid @RequestBody NotificationCreateRequest request) {
        NotificationResponse response = notificationService.createTestNotification(request);
        return ResponseEntity.status(201).body(ApiResponse.created(
                TestNotificationResponse.from(response), "테스트 알림을 성공적으로 생성했습니다."));
    }
}
