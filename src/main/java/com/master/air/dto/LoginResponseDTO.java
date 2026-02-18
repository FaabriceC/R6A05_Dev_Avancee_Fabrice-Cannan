package com.master.air.dto;

public class LoginResponseDTO {
    private String token;
    private Long userId;
    private String username;

    public LoginResponseDTO() {}

    public LoginResponseDTO(String token, Long userId, String username) {
        this.token = token;
        this.userId = userId;
        this.username = username;
    }

    public String getToken() { return token; }
    public void setToken(String t) { this.token = t; }
    public Long getUserId() { return userId; }
    public void setUserId(Long u) { this.userId = u; }
    public String getUsername() { return username; }
    public void setUsername(String u) { this.username = u; }
}
