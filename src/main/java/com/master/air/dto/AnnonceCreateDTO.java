package com.master.air.dto;

import jakarta.validation.constraints.*;

public record AnnonceCreateDTO(
    @NotBlank(message = "Le titre est obligatoire") @Size(max = 64) String title,
    @NotBlank(message = "La description est obligatoire") @Size(max = 256) String description,
    @NotBlank(message = "L'adresse est obligatoire") @Size(max = 64) String adress,
    @NotBlank(message = "L'email est obligatoire") @Email @Size(max = 64) String mail,
    @NotNull(message = "La categorie est obligatoire") Long categoryId
) {}
