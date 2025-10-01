package com.dosemeai.DoseMeAI.controllers.auth;

import com.dosemeai.DoseMeAI.domain.auth.JwtResponse;
import com.dosemeai.DoseMeAI.domain.auth.LoginRequest;
import com.dosemeai.DoseMeAI.domain.auth.RegisterRequest;
import com.dosemeai.DoseMeAI.services.auth.AuthService;
import com.dosemeai.DoseMeAI.utils.ResponseModel;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseModel<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            JwtResponse response = authService.login(request);
            return ResponseModel.ok(true,"Success Login.",response);
        } catch (Exception e) {
            return ResponseModel.error(false, "Login Failed: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseModel<JwtResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            JwtResponse response = authService.register(request);
            return ResponseModel.ok(true, "Success Register.", response);
        } catch (Exception e) {
            return ResponseModel.error(false, "Register Failed: " + e.getMessage());
        }
    }
}
