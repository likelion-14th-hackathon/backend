package com.example.likelion14th_hackathon.care.dto;

public record WeatherInfo(
        String weatherText, // 맑음, 구름 조금, 비
        double temperature,
        int humidity
) {}
