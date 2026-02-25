package com.master.air.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AnnonceDTO(
    Long id, String title, String description, String adress, String mail,
    String date, String status, Long categoryId, String categoryLabel,
    Long authorId, String authorUsername, Long version
) {}
