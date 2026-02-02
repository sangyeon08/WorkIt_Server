package com.jubilee.workit.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ApplicationDto {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String companyLogoUrl;
    private String status;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;

}