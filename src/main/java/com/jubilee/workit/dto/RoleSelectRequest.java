package com.jubilee.workit.dto;

public class RoleSelectRequest {
    private Long userId;
    private String role;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}