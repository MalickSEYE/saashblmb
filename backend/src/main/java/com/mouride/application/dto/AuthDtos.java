package com.mouride.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.UUID;

public class AuthDtos {

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class LoginRequest {
        @Email @NotBlank  private String email;
        @NotBlank @Size(min = 6) private String password;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class LoginResponse {
        private String accessToken;
        private String refreshToken;
        private String tokenType = "Bearer";
        private long   expiresIn;
        private String role;
        private String email;
        private UUID   userId;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class RegisterRequest {
        @Email @NotBlank  private String email;
        @NotBlank @Size(min = 8) private String password;
        @NotBlank private String nom;
        @NotBlank private String prenom;
        private String telephone;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class RefreshTokenRequest {
        @NotBlank private String refreshToken;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ForgotPasswordRequest {
        @Email @NotBlank private String email;
    }
}
