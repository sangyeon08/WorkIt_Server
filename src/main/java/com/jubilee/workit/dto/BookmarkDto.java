package com.jubilee.workit.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class BookmarkDto {
    private Long id;
    private JobCardDto job;
    private LocalDateTime createdAt;

}