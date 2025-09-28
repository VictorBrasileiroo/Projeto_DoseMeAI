package com.dosemeai.DoseMeAI.services;

import com.dosemeai.DoseMeAI.domain.auth.JwtResponse;
import com.dosemeai.DoseMeAI.domain.auth.LoginRequest;
import com.dosemeai.DoseMeAI.domain.auth.RegisterRequest;
import com.dosemeai.DoseMeAI.domain.users.UserModel;
import com.dosemeai.DoseMeAI.repositories.users.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public JwtResponse login(LoginRequest request) {
        // Autenticar usuário
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        // Buscar usuário
        UserModel user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Gerar token
        String token = jwtService.generateToken(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );

        return new JwtResponse(
                token,
                "Bearer",
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }

    public JwtResponse register(RegisterRequest request) {
        // Verificar se usuário já existe
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email já está em uso");
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username já está em uso");
        }

        // Criar novo usuário
        UserModel user = new UserModel();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setPhoneNumber(request.phoneNumber());
        user.setRole("USER"); // Role padrão

        UserModel savedUser = userRepository.save(user);

        // Gerar token
        String token = jwtService.generateToken(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole()
        );

        return new JwtResponse(
                token,
                "Bearer",
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }
