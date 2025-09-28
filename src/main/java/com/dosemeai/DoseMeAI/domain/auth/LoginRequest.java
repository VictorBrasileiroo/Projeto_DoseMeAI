package com.dosemeai.DoseMeAI.domain.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull @NotEmpty
        @Email(message = "Email deve ter formato válido")
        String email,

        @NotNull @NotEmpty
        String password
) {
}
