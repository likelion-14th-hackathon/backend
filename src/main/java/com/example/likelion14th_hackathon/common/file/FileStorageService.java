package com.example.likelion14th_hackathon.common.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.base-url}")
    private String baseUrl;

    /**
     * 업로드된 파일을 서버에 저장하고 접근 가능한 URL을 반환한다.
     */
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        validateImage(file);

        try {
            // 1. 고유한 파일명 생성 (원본명 그대로 쓰면 충돌/보안 문제)
            String savedName = UUID.randomUUID() + getExtension(file.getOriginalFilename());

            // 2. 저장 폴더 준비 (없으면 생성)
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            // 3. 실제 파일 저장
            Path target = uploadPath.resolve(savedName);
            file.transferTo(target.toFile());

            log.info("파일 저장 완료: {}", target);

            // 4. 웹 접근 URL 반환
            return baseUrl + "/" + savedName;

        } catch (IOException e) {
            log.error("파일 저장 실패: {}", e.getMessage());
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }
    }

    /**
     * 저장된 URL로부터 실제 파일 경로를 역추적한다. (Base64 변환 등에서 사용)
     */
    public Path resolvePath(String photoUrl) {
        if (photoUrl == null || !photoUrl.startsWith(baseUrl)) {
            throw new IllegalArgumentException("잘못된 파일 URL입니다: " + photoUrl);
        }
        String fileName = photoUrl.substring(baseUrl.length() + 1);   // baseUrl + "/" 제거
        return Paths.get(uploadDir).toAbsolutePath().normalize().resolve(fileName);
    }

    private void validateImage(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
        }
    }

    private String getExtension(String originalName) {
        if (originalName == null || !originalName.contains(".")) {
            return "";
        }
        return originalName.substring(originalName.lastIndexOf("."));
    }
}