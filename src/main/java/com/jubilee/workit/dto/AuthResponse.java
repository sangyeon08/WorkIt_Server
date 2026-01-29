package com.jubilee.workit.dto;

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

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
}
