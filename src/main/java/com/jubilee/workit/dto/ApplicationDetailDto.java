package com.jubilee.workit.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ApplicationDetailDto {
    private Long id;
    private JobCardDto job;
    private String coverLetter;
    private String phone;
    private String email;
    private String status;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;

}