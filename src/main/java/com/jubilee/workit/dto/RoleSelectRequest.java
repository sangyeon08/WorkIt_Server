package com.jubilee.workit.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoleSelectRequest {
    private Long userId;
    private String role;

}