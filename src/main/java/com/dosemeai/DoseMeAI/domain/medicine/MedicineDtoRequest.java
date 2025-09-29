package com.dosemeai.DoseMeAI.domain.medicine;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MedicineDtoRequest(
        @NotEmpty @NotNull
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        @Size(max = 256, message = "Description must be at most 256 characters")
        String description,

        @NotNull @NotEmpty
        @Positive(message = "Dosage must be a positive value")
        String dosage
){
}
