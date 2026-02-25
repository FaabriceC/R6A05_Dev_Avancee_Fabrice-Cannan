package com.master.air.dto;

public record LoginResponseDTO(String token, Long userId, String username, String role) {}
