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
    private JobCardDto job;
    private String status;
    private String displayStatus;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;

}
