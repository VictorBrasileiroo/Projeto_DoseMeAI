package com.dosemeai.DoseMeAI.domain.users;

import jakarta.validation.constraints.*;

public record UserDtoRequest(
        @NotNull @NotEmpty
        @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
        String username,

        @NotNull @NotEmpty
        @Email
        String email,

        @NotNull @NotEmpty
        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$", message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character")
        String password,

        @NotNull @NotEmpty
        @Pattern(regexp = "^(USER|ADMIN)$", message = "Role must be either USER or ADMIN")
        String role,

        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
        String phoneNumber
){
}
