package com.example.likelion14th_hackathon.notification.service;

public class NotificationNotFoundException extends RuntimeException {
    public NotificationNotFoundException() {
        super("존재하지 않는 알림 ID입니다.");
    }
}
