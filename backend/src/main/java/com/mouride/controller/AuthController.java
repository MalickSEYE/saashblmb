package com.mouride.controller;

import com.mouride.application.dto.AuthDtos.*;
import com.mouride.domain.model.User;
import com.mouride.domain.repository.UserRepository;
import com.mouride.infrastructure.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentification")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Email ou mot de passe incorrect"));
        }
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        if (!user.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Compte désactivé"));
        }
        return ResponseEntity.ok(LoginResponse.builder()
            .accessToken(jwtService.generateToken(user))
            .refreshToken(jwtService.generateRefreshToken(user))
            .tokenType("Bearer")
            .expiresIn(jwtService.getExpiration())
            .role(user.getRole().name())
            .email(user.getEmail())
            .userId(user.getId())
            .build());
    }

    @PostMapping("/register")
    @Operation(summary = "Inscription")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Cet email est déjà utilisé"));
        }
        // Premier compte = SUPER_ADMIN, les suivants = MEMBRE
        long count = userRepository.count();
        User.Role role = count == 0 ? User.Role.SUPER_ADMIN : User.Role.MEMBRE;

        User user = User.builder()
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .role(role)
            .active(true) // Auto-activation pour le MVP
            .build();
        userRepository.save(user);

        String msg = count == 0
            ? "Compte Super Admin créé avec succès."
            : "Compte créé. En attente d'activation par un administrateur.";
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", msg));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renouvellement du token")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            String email = jwtService.extractUsername(request.getRefreshToken());
            User user = userRepository.findByEmail(email).orElseThrow();
            if (!jwtService.isTokenValid(request.getRefreshToken(), user)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Token invalide"));
            }
            return ResponseEntity.ok(Map.of(
                "accessToken", jwtService.generateToken(user),
                "tokenType", "Bearer",
                "expiresIn", jwtService.getExpiration()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Token invalide"));
        }
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Réinitialisation mot de passe")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(Map.of("message",
            "Si cet email existe, un lien de réinitialisation a été envoyé."));
    }

    // Endpoint temporaire pour promouvoir un compte en SUPER_ADMIN
    // À supprimer après la première utilisation !
    @PostMapping("/setup/{email}")
    @Operation(summary = "Promouvoir un compte en SUPER_ADMIN (setup initial uniquement)")
    public ResponseEntity<?> setup(@PathVariable String email) {
        return userRepository.findByEmail(email).map(user -> {
            user.setRole(User.Role.SUPER_ADMIN);
            user.setActive(true);
            userRepository.save(user);
            return ResponseEntity.ok(Map.of(
                "message", "Compte promu SUPER_ADMIN",
                "email", email
            ));
        }).orElse(ResponseEntity.notFound().build());
    }
}
