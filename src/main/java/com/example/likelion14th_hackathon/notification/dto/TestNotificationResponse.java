package com.example.likelion14th_hackathon.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TestNotificationResponse {
    private Long notificationId;
    private Long userProductId;
    private String triggerType;
    private String title;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

    public static TestNotificationResponse from(NotificationResponse notification) {
        return new TestNotificationResponse(
                notification.getNotificationId(), notification.getUserProductId(),
                notification.getTriggerType().name(), notification.getTitle(), notification.getBody(),
                notification.isRead(), notification.getCreatedAt());
    }
}
