package com.example.likelion14th_hackathon.notification.service;

import com.example.likelion14th_hackathon.care.dto.WeatherInfo;
import com.example.likelion14th_hackathon.care.service.WeatherApiService;
import com.example.likelion14th_hackathon.catalog.domain.Product;
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
            String productName = getProductName(ownedProduct);
            notificationService.createScheduledNotificationIfAbsent(
                    ownedProduct,
                    NotificationTriggerType.WEATHER_ALERT,
                    "날씨 기반 케어 알림",
                    String.format("현재 %s, 습도 %d%%입니다. %s 관리에 주의해 주세요.",
                            weather.weatherText(), weather.humidity(), productName),
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

            String productName = getProductName(ownedProduct);
            notificationService.createScheduledNotificationIfAbsent(
                    ownedProduct,
                    NotificationTriggerType.CARE_CYCLE,
                    "정기 케어 알림",
                    String.format("%s의 정기 케어 주기가 도래했습니다. 소재에 맞는 관리 상태를 확인해 주세요.", productName),
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

    private String getProductName(OwnedProduct ownedProduct) {
        Product product = ownedProduct.getProduct();
        if (product == null || product.getName() == null || product.getName().isBlank()) {
            return "등록 제품";
        }
        return product.getName();
    }
}
