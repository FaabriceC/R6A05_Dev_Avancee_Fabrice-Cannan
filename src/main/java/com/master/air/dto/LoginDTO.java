package com.master.air.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginDTO {

    @NotBlank(message = "Le username est obligatoire")
    private String username;

    @NotBlank(message = "Le password est obligatoire")
    private String password;

    public LoginDTO() {}
    public LoginDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public void setUsername(String u) { this.username = u; }
    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }
}
