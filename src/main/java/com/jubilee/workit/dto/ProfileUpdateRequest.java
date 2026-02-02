package com.jubilee.workit.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProfileUpdateRequest {
    private Long locationId;
    private String preferences;

}
