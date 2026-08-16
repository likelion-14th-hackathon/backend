package com.example.likelion14th_hackathon.common.security;

import com.example.likelion14th_hackathon.common.api.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JwtTokenProvider {

    private static final Pattern MEMBER_ID_PATTERN = Pattern.compile("\"memberId\"\\s*:\\s*(\\d+)");
    private static final Pattern EXP_PATTERN = Pattern.compile("\"exp\"\\s*:\\s*(\\d+)");

    private final byte[] signingKey;

    public JwtTokenProvider(@Value("${jwt.secret:change-this-secret-before-deploy}") String secret) {
        this.signingKey = sha256(secret);
    }

    public Long extractMemberIdFromAuthorization(String authorizationHeader) {
        String token = resolveBearerToken(authorizationHeader);
        validateToken(token);
        String payloadJson = decodePayload(token);

        Matcher matcher = MEMBER_ID_PATTERN.matcher(payloadJson);
        if (!matcher.find()) {
            throw new UnauthorizedException("토큰에 회원 정보가 없습니다.");
        }

        return Long.valueOf(matcher.group(1));
    }

    private String resolveBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new UnauthorizedException("Authorization 헤더가 필요합니다.");
        }

        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Bearer 토큰 형식이 아닙니다.");
        }

        String token = authorizationHeader.substring(7).trim();
        if (token.isBlank()) {
            throw new UnauthorizedException("토큰이 비어 있습니다.");
        }
        return token;
    }

    private void validateToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new UnauthorizedException("유효하지 않은 토큰입니다.");
        }

        String unsignedToken = parts[0] + "." + parts[1];
        String expectedSignature = sign(unsignedToken);
        if (!MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                parts[2].getBytes(StandardCharsets.UTF_8)
        )) {
            throw new UnauthorizedException("토큰 서명이 유효하지 않습니다.");
        }

        validateExpiration(decodePayload(token));
    }

    private void validateExpiration(String payloadJson) {
        Matcher matcher = EXP_PATTERN.matcher(payloadJson);
        if (!matcher.find()) {
            throw new UnauthorizedException("토큰 만료 정보가 없습니다.");
        }

        long expiresAt = Long.parseLong(matcher.group(1));
        if (expiresAt < Instant.now().getEpochSecond()) {
            throw new UnauthorizedException("만료된 토큰입니다.");
        }
    }

    private String decodePayload(String token) {
        String[] parts = token.split("\\.");
        try {
            byte[] payload = Base64.getUrlDecoder().decode(parts[1]);
            return new String(payload, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            throw new UnauthorizedException("토큰 payload를 해석할 수 없습니다.");
        }
    }

    private String sign(String unsignedToken) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(signingKey, "HmacSHA256"));
            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(mac.doFinal(unsignedToken.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("JWT 서명 검증에 실패했습니다.", exception);
        }
    }

    private byte[] sha256(String value) {
        try {
            return MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (Exception exception) {
            throw new IllegalStateException("JWT Secret 처리에 실패했습니다.", exception);
        }
    }
}
