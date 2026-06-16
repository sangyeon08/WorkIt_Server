package com.jubilee.workit.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class RecentlyViewedJobDto {
    private Long id;
    private JobCardDto job;
    private LocalDateTime viewedAt;
}
