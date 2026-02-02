package com.jubilee.workit.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class NotificationDto {
    private Long id;
    private String message;
    private String type;
    private boolean read;
    private LocalDateTime createdAt;

}
