package com.jubilee.workit.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class JobUpdateRequest {
    private String title;
    private String description;
    private Long locationId;
    private Long companyId;
    private BigDecimal compensationAmount;
    private String compensationType;
    private String jobType;
    private String durationType;
    private String imageUrl;
    private List<Long> categoryIds;
    private LocalDateTime expiresAt;
    private Boolean hot;
}
