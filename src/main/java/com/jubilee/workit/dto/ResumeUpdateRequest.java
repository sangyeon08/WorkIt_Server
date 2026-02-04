package com.jubilee.workit.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResumeUpdateRequest {
    private String title;
    private String summary;
    private String experience;
    private String education;
    private String skills;
    private String certifications;
    private String resumeFileUrl;
}