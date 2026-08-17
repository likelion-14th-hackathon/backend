package com.example.likelion14th_hackathon.notification.service;

import com.example.likelion14th_hackathon.care.dto.WeatherInfo;
import com.example.likelion14th_hackathon.care.service.WeatherApiService;
import com.example.likelion14th_hackathon.common.api.ResourceNotFoundException;
import com.example.likelion14th_hackathon.common.api.UnauthorizedException;
import com.example.likelion14th_hackathon.mypage.domain.OwnedProduct;
import com.example.likelion14th_hackathon.mypage.repository.OwnedProductRepository;
import com.example.likelion14th_hackathon.notification.domain.Notification;
import com.example.likelion14th_hackathon.notification.domain.NotificationTriggerType;
import com.example.likelion14th_hackathon.notification.dto.NotificationCreateRequest;
import com.example.likelion14th_hackathon.notification.dto.NotificationResponse;
import com.example.likelion14th_hackathon.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final double SEOUL_LATITUDE = 37.5665;
    private static final double SEOUL_LONGITUDE = 126.9780;

    private final NotificationRepository notificationRepository;
    private final OwnedProductRepository ownedProductRepository;
    private final WeatherApiService weatherApiService;
    private final NotificationMessageGenerator notificationMessageGenerator;

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications() {
        return notificationRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByMemberId(Long memberId) {
        return notificationRepository.findByMemberIdOrderByCreatedAtDesc(memberId).stream()
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
    public NotificationResponse createTestNotification(Long memberId, NotificationCreateRequest request) {
        OwnedProduct ownedProduct = getOwnedProductForMember(memberId, request.getUserProductId());
        String title = notificationMessageGenerator.createTitle(request.getTriggerType());
        String message = createMessage(ownedProduct, request.getTriggerType());

        Notification notification = new Notification(
                memberId,
                ownedProduct.getId(),
                request.getTriggerType(),
                title,
                message
        );
        return NotificationResponse.from(notificationRepository.save(notification));
    }

    @Transactional
    public boolean createScheduledNotificationIfAbsent(
            OwnedProduct ownedProduct,
            NotificationTriggerType triggerType,
            String title,
            String message,
            LocalDate date
    ) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        boolean alreadyCreated = notificationRepository.existsByUserProductIdAndTriggerTypeAndCreatedAtBetween(
                ownedProduct.getId(), triggerType, startOfDay, endOfDay);

        if (alreadyCreated) {
            return false;
        }

        Notification notification = new Notification(
                ownedProduct.getMemberId(),
                ownedProduct.getId(),
                triggerType,
                title,
                message
        );
        notificationRepository.save(notification);
        return true;
    }

    private String createMessage(OwnedProduct ownedProduct, NotificationTriggerType triggerType) {
        if (triggerType == NotificationTriggerType.WEATHER_ALERT) {
            WeatherInfo weather = weatherApiService.getWeather(SEOUL_LATITUDE, SEOUL_LONGITUDE);
            return notificationMessageGenerator.createWeatherAlertMessage(ownedProduct, weather);
        }
        if (triggerType == NotificationTriggerType.CARE_CYCLE) {
            return notificationMessageGenerator.createCareCycleMessage(ownedProduct);
        }
        throw new IllegalArgumentException("지원하지 않는 알림 트리거입니다. triggerType=" + triggerType);
    }

    private OwnedProduct getOwnedProductForMember(Long memberId, Long userProductId) {
        OwnedProduct ownedProduct = ownedProductRepository.findById(userProductId)
                .orElseThrow(() -> new ResourceNotFoundException("등록 제품을 찾을 수 없습니다."));

        if (!ownedProduct.getMemberId().equals(memberId)) {
            throw new UnauthorizedException("해당 등록 제품에 접근할 권한이 없습니다.");
        }

        return ownedProduct;
    }
}
