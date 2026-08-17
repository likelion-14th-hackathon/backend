package com.example.likelion14th_hackathon.notification.service;

import com.example.likelion14th_hackathon.care.dto.WeatherInfo;
import com.example.likelion14th_hackathon.care.service.WeatherApiService;
import com.example.likelion14th_hackathon.mypage.domain.OwnedProduct;
import com.example.likelion14th_hackathon.mypage.domain.OwnershipStatus;
import com.example.likelion14th_hackathon.mypage.repository.OwnedProductRepository;
import com.example.likelion14th_hackathon.notification.domain.NotificationTriggerType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private static final double SEOUL_LATITUDE = 37.5665;
    private static final double SEOUL_LONGITUDE = 126.9780;
    private static final int CARE_CYCLE_DAYS = 30;
    private static final int HUMIDITY_WARNING_THRESHOLD = 75;

    private final OwnedProductRepository ownedProductRepository;
    private final NotificationService notificationService;
    private final WeatherApiService weatherApiService;
    private final NotificationMessageGenerator notificationMessageGenerator;

    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Seoul")
    public void createWeatherAlertNotifications() {
        WeatherInfo weather;
        try {
            weather = weatherApiService.getWeather(SEOUL_LATITUDE, SEOUL_LONGITUDE);
        } catch (RuntimeException exception) {
            log.warn("날씨 알림 스케줄러가 날씨 조회에 실패했습니다.", exception);
            return;
        }

        if (!requiresWeatherAlert(weather)) {
            return;
        }

        LocalDate today = LocalDate.now();
        List<OwnedProduct> ownedProducts = ownedProductRepository.findByStatus(OwnershipStatus.OWNED);
        for (OwnedProduct ownedProduct : ownedProducts) {
            notificationService.createScheduledNotificationIfAbsent(
                    ownedProduct,
                    NotificationTriggerType.WEATHER_ALERT,
                    notificationMessageGenerator.createTitle(NotificationTriggerType.WEATHER_ALERT),
                    notificationMessageGenerator.createWeatherAlertMessage(ownedProduct, weather),
                    today
            );
        }
    }

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void createCareCycleNotifications() {
        LocalDate today = LocalDate.now();
        List<OwnedProduct> ownedProducts = ownedProductRepository.findByStatus(OwnershipStatus.OWNED);

        for (OwnedProduct ownedProduct : ownedProducts) {
            if (!isCareCycleDue(ownedProduct, today)) {
                continue;
            }

            notificationService.createScheduledNotificationIfAbsent(
                    ownedProduct,
                    NotificationTriggerType.CARE_CYCLE,
                    notificationMessageGenerator.createTitle(NotificationTriggerType.CARE_CYCLE),
                    notificationMessageGenerator.createCareCycleMessage(ownedProduct),
                    today
            );
        }
    }

    private boolean requiresWeatherAlert(WeatherInfo weather) {
        return weather.weatherText().contains("비")
                || weather.weatherText().contains("눈")
                || weather.weatherText().contains("뇌우")
                || weather.humidity() >= HUMIDITY_WARNING_THRESHOLD;
    }

    private boolean isCareCycleDue(OwnedProduct ownedProduct, LocalDate today) {
        if (ownedProduct.getPurchasedAt() == null) {
            return false;
        }

        long daysSincePurchase = ChronoUnit.DAYS.between(ownedProduct.getPurchasedAt().toLocalDate(), today);
        return daysSincePurchase > 0 && daysSincePurchase % CARE_CYCLE_DAYS == 0;
    }

}
