package com.example.likelion14th_hackathon.common.security;

import com.example.likelion14th_hackathon.member.domain.Member;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

@Component
public class JwtTokenProvider {

    private final byte[] signingKey;
    private final long expirationSeconds;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-minutes:120}") long expirationMinutes
    ) {
        this.signingKey = sha256(secret);
        this.expirationSeconds = expirationMinutes * 60;
    }

    public String createAccessToken(Member member) {
        long now = Instant.now().getEpochSecond();
        long expiresAt = now + expirationSeconds;

        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payloadJson = """
                {"sub":"%s","memberId":%d,"email":"%s","nickname":"%s","iat":%d,"exp":%d}
                """.formatted(
                member.getId(),
                member.getId(),
                escapeJson(member.getEmail()),
                escapeJson(member.getNickname()),
                now,
                expiresAt
        ).trim();

        String header = base64Url(headerJson.getBytes(StandardCharsets.UTF_8));
        String payload = base64Url(payloadJson.getBytes(StandardCharsets.UTF_8));
        String unsignedToken = header + "." + payload;

        return unsignedToken + "." + sign(unsignedToken);
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    private String sign(String unsignedToken) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(signingKey, "HmacSHA256"));
            return base64Url(mac.doFinal(unsignedToken.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("JWT 서명 생성에 실패했습니다.", e);
        }
    }

    private byte[] sha256(String value) {
        try {
            return MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("JWT Secret 처리에 실패했습니다.", e);
        }
    }

    private String base64Url(byte[] value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value);
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
