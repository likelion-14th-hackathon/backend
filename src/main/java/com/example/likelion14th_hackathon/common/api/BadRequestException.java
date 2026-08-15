package com.example.likelion14th_hackathon.common.api;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
