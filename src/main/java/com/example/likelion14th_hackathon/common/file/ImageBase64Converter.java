package com.example.likelion14th_hackathon.common.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageBase64Converter {

    private final FileStorageService fileStorageService;

    /**
     * 저장된 사진 URL 목록을 OpenAI Vision이 받는 data URL 형식으로 변환한다.
     * 읽기 실패한 파일은 건너뛴다.
     */
    public List<String> convertAll(List<String> photoUrls) {
        List<String> result = new ArrayList<>();

        for (String photoUrl : photoUrls) {
            try {
                result.add(convert(photoUrl));
            } catch (Exception e) {
                log.warn("이미지 Base64 변환 실패, 건너뜁니다. url={}, error={}", photoUrl, e.getMessage());
            }
        }
        return result;
    }

    /** 단일 사진 URL → data:image/jpeg;base64,... 형식 문자열 */
    public String convert(String photoUrl) throws IOException {
        Path path = fileStorageService.resolvePath(photoUrl);

        if (!Files.exists(path)) {
            throw new IOException("파일이 존재하지 않습니다: " + path);
        }

        byte[] bytes = Files.readAllBytes(path);
        String base64 = Base64.getEncoder().encodeToString(bytes);
        String mimeType = detectMimeType(photoUrl);

        return "data:" + mimeType + ";base64," + base64;
    }

    /** 확장자로 MIME 타입 추정 */
    private String detectMimeType(String photoUrl) {
        String lower = photoUrl.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".gif")) return "image/gif";
        return "image/jpeg";   // 기본값
    }
}