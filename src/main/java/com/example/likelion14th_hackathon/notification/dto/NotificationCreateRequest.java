package com.example.likelion14th_hackathon.notification.dto;

import com.example.likelion14th_hackathon.notification.domain.NotificationTriggerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationCreateRequest {
    @NotNull
    private Long memberId;

    @NotNull
    private Long userProductId;

    @NotNull
    private NotificationTriggerType triggerType;

    @NotBlank
    private String title;

    @NotBlank
    private String message;
}
