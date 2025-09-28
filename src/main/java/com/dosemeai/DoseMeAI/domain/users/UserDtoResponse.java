package com.dosemeai.DoseMeAI.domain.users;

import java.util.UUID;

public record UserDtoResponse(
        UUID id,
        String username,
        String email,
        String phoneNumber,
        String role
){
}
