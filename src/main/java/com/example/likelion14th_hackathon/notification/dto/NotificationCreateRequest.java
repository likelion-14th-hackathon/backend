package com.example.likelion14th_hackathon.notification.dto;

import com.example.likelion14th_hackathon.notification.domain.NotificationTriggerType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationCreateRequest {
    @NotNull
    private Long userProductId;

    @NotNull
    private NotificationTriggerType triggerType;
}
