package com.jubilee.workit.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EmployerApplicationDto {
    private Long id;
    private Long jobId;
    private String jobTitle;

    private Long applicantId;
    private String applicantEmail;
    private String coverLetter;
    private String phone;
    private String email;

    private ResumeDto resume;

    private String status;
    private String displayStatus;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
}
