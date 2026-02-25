package com.master.air.dto;

import com.master.air.model.AnnonceStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AnnonceUpdateRequestDTO {

    @NotBlank
    @Size(max = 64)
    public String title;

    @NotBlank
    @Size(max = 256)
    public String description;

    @NotBlank
    @Size(max = 64)
    public String adress;

    @NotBlank
    @Email
    @Size(max = 64)
    public String mail;

    @NotNull
    public AnnonceStatus status;

    @NotNull
    public Long authorId;

    @NotNull
    public Long categoryId;
}