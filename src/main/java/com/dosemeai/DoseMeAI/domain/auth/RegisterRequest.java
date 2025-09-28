package com.dosemeai.DoseMeAI.domain.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotNull @NotEmpty
        @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
        String username,

        @NotNull @NotEmpty
        @Email(message = "Email deve ter formato válido")
        String email,

        @NotNull @NotEmpty
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
                 message = "Senha deve conter pelo menos 1 letra maiúscula, 1 minúscula e 1 número")
        String password,

        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone deve ter formato válido")
        String phoneNumber
) {
}



