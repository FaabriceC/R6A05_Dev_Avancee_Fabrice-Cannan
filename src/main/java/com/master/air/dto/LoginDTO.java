package com.master.air.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
    @NotBlank(message = "Le username est obligatoire") String username,
    @NotBlank(message = "Le password est obligatoire") String password
) {}
