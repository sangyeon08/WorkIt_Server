package com.jubilee.workit.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ResumeDto {
    private Long id;
    private Long userId;
    private String title;
    private String summary;
    private String experience;
    private String education;
    private String skills;
    private String certifications;
    private String resumeFileUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}