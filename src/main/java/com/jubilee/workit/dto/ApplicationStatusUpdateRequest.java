package com.jubilee.workit.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationStatusUpdateRequest {
    private String status; // REVIEWING, ACCEPTED, REJECTED
}
