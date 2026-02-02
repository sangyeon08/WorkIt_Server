package com.jubilee.workit.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthResponse {
    private Long userId;
    private String email;
    private String role;
    private String message;
    private String accessToken;
    private String tokenType = "Bearer";
    private Long expiresIn;

    public AuthResponse() {}

    public AuthResponse(Long userId, String email, String role, String message) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.message = message;
    }

    public AuthResponse(Long userId, String email, String role, String message,
                        String accessToken, Long expiresIn) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.message = message;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }

}
