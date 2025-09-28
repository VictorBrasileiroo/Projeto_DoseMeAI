package com.dosemeai.DoseMeAI.domain.auth;

import java.util.UUID;

public record JwtResponse(
        String token,
        String type,
        UUID id,
        String username,
        String email,
        String role
){
}
