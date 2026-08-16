package com.example.likelion14th_hackathon.notification.repository;

import com.example.likelion14th_hackathon.notification.domain.Notification;
import com.example.likelion14th_hackathon.notification.domain.NotificationTriggerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByOrderByCreatedAtDesc();

    List<Notification> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    boolean existsByUserProductIdAndTriggerTypeAndCreatedAtBetween(
            Long userProductId,
            NotificationTriggerType triggerType,
            LocalDateTime start,
            LocalDateTime end
    );
}
