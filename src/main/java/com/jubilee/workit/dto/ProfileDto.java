package com.jubilee.workit.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ProfileDto {
    private Long id;
    private String email;
    private String role;
    private String loginType;
    private LocationDto location;
    private String preferences;
    private LocalDateTime createdAt;

}