package com.master.air.dto;

public record AnnoncePatchDTO(
    String title, String description, String adress, String mail, Long categoryId, String status
) {}
