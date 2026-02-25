package com.master.air.dto;

public class LoginResponseDTO {
    public String token;
    public String tokenType = "Bearer";

    public Long userId;
    public String username;
    public String role;

    public long expiresInMs;

    public LoginResponseDTO(String token, Long userId, String username, String role, long expiresInMs) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.expiresInMs = expiresInMs;
    }
}