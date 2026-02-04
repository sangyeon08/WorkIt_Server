package com.jubilee.workit.controller;

import com.jubilee.workit.dto.ImageUploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Value("${workit.image.upload-dir:uploads/images}")
    private String uploadDir;

    // 이미지 업로드 (multipart)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ImageUploadResponse uploadImage(@RequestParam("file") MultipartFile file) {
        // 파일 검증
        if (file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "파일이 비어있습니다.");
        }

        // 파일 크기 제한 (10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "파일 크기는 10MB를 초과할 수 없습니다.");
        }

        // 파일 확장자 검증
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isValidFileExtension(originalFilename)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "지원하지 않는 파일 형식입니다. (jpg, jpeg, png, pdf만 가능)");
        }

        try {
            // 파일명 생성 (UUID)
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String savedFilename = UUID.randomUUID().toString() + extension;

            // 저장 경로
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 파일 저장
            Path filePath = uploadPath.resolve(savedFilename);
            Files.copy(file.getInputStream(), filePath);

            // 응답
            ImageUploadResponse response = new ImageUploadResponse();
            response.setUrl("/api/images/" + savedFilename);
            response.setOriginalName(originalFilename);
            response.setSize(file.getSize());
            response.setContentType(file.getContentType());

            return response;

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 이미지 조회
    @GetMapping("/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);

            if (!Files.exists(filePath)) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다.");
            }

            byte[] imageBytes = Files.readAllBytes(filePath);

            // Content-Type 설정
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(imageBytes);

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "파일 읽기 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // Base64 업로드 (선택)
    @PostMapping(value = "/upload/base64", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ImageUploadResponse uploadBase64(@RequestBody Base64UploadRequest request) {
        try {
            // Base64 디코딩
            String base64Data = request.getBase64Data();
            if (base64Data == null || base64Data.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "base64Data가 비어있습니다.");
            }

            // "data:image/png;base64," 부분 제거
            if (base64Data.contains(",")) {
                base64Data = base64Data.split(",")[1];
            }

            byte[] decodedBytes = java.util.Base64.getDecoder().decode(base64Data);

            // 파일명 생성
            String extension = request.getExtension() != null ? request.getExtension() : ".png";
            String savedFilename = UUID.randomUUID().toString() + extension;

            // 저장 경로
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 파일 저장
            Path filePath = uploadPath.resolve(savedFilename);
            Files.write(filePath, decodedBytes);

            // 응답
            ImageUploadResponse response = new ImageUploadResponse();
            response.setUrl("/api/images/" + savedFilename);
            response.setOriginalName(savedFilename);
            response.setSize((long) decodedBytes.length);
            response.setContentType("image/" + extension.replace(".", ""));

            return response;

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Base64 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 파일 확장자 검증
    private boolean isValidFileExtension(String filename) {
        String lowerFilename = filename.toLowerCase();
        return lowerFilename.endsWith(".jpg") ||
                lowerFilename.endsWith(".jpeg") ||
                lowerFilename.endsWith(".png") ||
                lowerFilename.endsWith(".pdf");
    }

    // Base64 업로드 요청 DTO
    public static class Base64UploadRequest {
        private String base64Data;
        private String extension;

        public String getBase64Data() { return base64Data; }
        public void setBase64Data(String base64Data) { this.base64Data = base64Data; }

        public String getExtension() { return extension; }
        public void setExtension(String extension) { this.extension = extension; }
    }
}