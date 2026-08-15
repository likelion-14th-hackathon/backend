package com.example.likelion14th_hackathon.notification.dto;

import com.example.likelion14th_hackathon.notification.domain.Notification;
import com.example.likelion14th_hackathon.notification.domain.NotificationTriggerType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class NotificationResponse {
    private Long notificationId;
    private Long userProductId;
    private NotificationTriggerType triggerType;
    private String title;
    private String body;
    private boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(), notification.getUserProductId(), notification.getTriggerType(),
                notification.getTitle(), notification.getMessage(), notification.isRead(), notification.getCreatedAt());
    }
}
