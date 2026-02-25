package com.master.air.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {
    @NotBlank public String username;
    @NotBlank public String password;
}