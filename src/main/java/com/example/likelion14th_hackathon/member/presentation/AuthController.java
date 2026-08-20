package com.example.likelion14th_hackathon.member.presentation;

import com.example.likelion14th_hackathon.common.response.ApiResponse;
import com.example.likelion14th_hackathon.member.application.AuthService;
import com.example.likelion14th_hackathon.member.presentation.dto.AuthResponse;
import com.example.likelion14th_hackathon.member.presentation.dto.LoginRequest;
import com.example.likelion14th_hackathon.member.presentation.dto.SignupRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/auth", "/api/auth"})
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ApiResponse<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ApiResponse.success(authService.signup(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }
}
