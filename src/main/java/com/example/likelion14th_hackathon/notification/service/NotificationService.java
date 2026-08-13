package com.example.likelion14th_hackathon.notification.service;

import com.example.likelion14th_hackathon.notification.domain.Notification;
import com.example.likelion14th_hackathon.notification.dto.NotificationCreateRequest;
import com.example.likelion14th_hackathon.notification.dto.NotificationResponse;
import com.example.likelion14th_hackathon.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications() {
        return notificationRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @Transactional
    public NotificationResponse markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(NotificationNotFoundException::new);
        notification.markAsRead();
        return NotificationResponse.from(notification);
    }

    @Transactional
    public NotificationResponse createTestNotification(NotificationCreateRequest request) {
        Notification notification = new Notification(
                request.getMemberId(), request.getUserProductId(), request.getTriggerType(),
                request.getTitle(), request.getMessage());
        return NotificationResponse.from(notificationRepository.save(notification));
    }
}
