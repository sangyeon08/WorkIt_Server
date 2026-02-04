package com.jubilee.workit.dto;

public class ImageUploadResponse {

    private String url;
    private String originalName;
    private Long size;
    private String contentType;

    // 기본 생성자
    public ImageUploadResponse() {}

    // 전체 생성자
    public ImageUploadResponse(String url, String originalName, Long size, String contentType) {
        this.url = url;
        this.originalName = originalName;
        this.size = size;
        this.contentType = contentType;
    }

    // Getters and Setters
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }

    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
}