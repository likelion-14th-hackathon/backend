package com.example.likelion14th_hackathon.care.service;

import com.example.likelion14th_hackathon.care.dto.WeatherInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class WeatherApiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String API_URL = "https://api.open-meteo.com/v1/forecast";

    public WeatherInfo getWeather(double lat, double lon) {
        String url = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("latitude", lat)
                .queryParam("longitude", lon)
                .queryParam("current", "temperature_2m,relative_humidity_2m,weather_code")
                .toUriString();

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            Map<String, Object> current = (Map<String, Object>) response.get("current");

            double temp = ((Number) current.get("temperature_2m")).doubleValue();
            int humidity = ((Number) current.get("relative_humidity_2m")).intValue();
            int code = ((Number) current.get("weather_code")).intValue();

            return new WeatherInfo(parseWeatherCode(code), temp, humidity);
        } catch (Exception e) {
            // 외부 API 장애 시 RuntimeException 전파
            throw new RuntimeException("날씨 API 연동 실패: " + e.getMessage());
        }
    }

    private String parseWeatherCode(int code) {
        return switch (code) {
            case 0 -> "맑음";
            case 1, 2, 3 -> "구름 조금 / 흐림";
            case 45, 48 -> "안개";
            case 51, 53, 55, 61, 63, 65, 80, 81, 82 -> "비";
            case 71, 73, 75, 85, 86 -> "눈";
            case 95, 96, 99 -> "뇌우";
            default -> "알 수 없음";
        };
    }
}