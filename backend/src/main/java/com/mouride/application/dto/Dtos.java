package com.mouride.application.dto;

import com.mouride.domain.model.Cotisation;
import com.mouride.domain.model.Evenement;
import com.mouride.domain.model.Membre;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

// ────────────────────────────────────────────────────────────
// AUTH
// ────────────────────────────────────────────────────────────
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
        @NotBlank @Size(min = 8, message = "Mot de passe : 8 caractères minimum")
        private String password;
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

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ResetPasswordRequest {
        @NotBlank private String token;
        @NotBlank @Size(min = 8) private String newPassword;
    }
}

// ────────────────────────────────────────────────────────────
// MEMBRE
// ────────────────────────────────────────────────────────────
public class MembreDtos {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        @NotBlank private String nom;
        @NotBlank private String prenom;
        @Email   private String email;
        private String telephone;
        private String adresse;
        private LocalDate dateNaissance;
        private Membre.Sexe sexe;
        private String ville;
        private String pays;
        private String profession;
        private String fonctionReligieuse;
        private UUID dahiraId;
        private Membre.Statut statut;
        private String notes;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private UUID id;
        private String numeroMembre;
        private String nom;
        private String prenom;
        private String email;
        private String telephone;
        private String adresse;
        private LocalDate dateNaissance;
        private Membre.Sexe sexe;
        private String ville;
        private String pays;
        private String profession;
        private String fonctionReligieuse;
        private UUID dahiraId;
        private String dahiraNom;
        private Membre.Statut statut;
        private String photoUrl;
        private LocalDate dateAdhesion;
        private String notes;
        private LocalDateTime createdAt;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Stats {
        private long total;
        private long actifs;
        private long inactifs;
        private long enAttente;
        private long suspendus;
        private long nouveauxCeMois;
    }
}

// ────────────────────────────────────────────────────────────
// DAHIRA
// ────────────────────────────────────────────────────────────
public class DahiraDtos {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        @NotBlank private String nom;
        private String code;
        private String description;
        private String ville;
        private String pays;
        private String adresse;
        private String telephone;
        private String email;
        private UUID responsableId;
        private LocalDate dateCreation;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private UUID id;
        private String nom;
        private String code;
        private String description;
        private String ville;
        private String pays;
        private String adresse;
        private String telephone;
        private String email;
        private UUID responsableId;
        private String responsableNom;
        private LocalDate dateCreation;
        private boolean active;
        private long nombreMembres;
        private BigDecimal totalCotisations;
    }
}

// ────────────────────────────────────────────────────────────
// COTISATION
// ────────────────────────────────────────────────────────────
public class CotisationDtos {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        @NotNull private UUID membreId;
        private UUID dahiraId;
        @NotNull @DecimalMin("1") private BigDecimal montant;
        private Cotisation.Type type;
        private String periode;
        private Cotisation.MoyenPaiement moyenPaiement;
        private String referencePaiement;
        private String notes;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private UUID id;
        private UUID membreId;
        private String membreNomComplet;
        private UUID dahiraId;
        private String dahiraNom;
        private BigDecimal montant;
        private Cotisation.Type type;
        private String periode;
        private Cotisation.Statut statut;
        private Cotisation.MoyenPaiement moyenPaiement;
        private String referencePaiement;
        private String notes;
        private LocalDateTime datePaiement;
        private LocalDateTime dateValidation;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FinanceStats {
        private BigDecimal totalValide;
        private BigDecimal totalCeMois;
        private BigDecimal totalCetteAnnee;
        private long nbEnAttente;
        private long nbValide;
        private long nbRejete;
    }
}

// ────────────────────────────────────────────────────────────
// EVENEMENT
// ────────────────────────────────────────────────────────────
public class EvenementDtos {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        @NotBlank private String titre;
        private Evenement.Type type;
        private String description;
        @NotNull private LocalDateTime dateDebut;
        private LocalDateTime dateFin;
        private String lieu;
        private Integer capaciteMax;
        private BigDecimal prixEntree;
        private UUID dahiraId;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private UUID id;
        private String titre;
        private Evenement.Type type;
        private String description;
        private LocalDateTime dateDebut;
        private LocalDateTime dateFin;
        private String lieu;
        private Integer capaciteMax;
        private BigDecimal prixEntree;
        private String imageUrl;
        private UUID dahiraId;
        private String dahiraNom;
        private Evenement.Statut statut;
        private long nbInscrits;
        private long nbPresents;
        private LocalDateTime createdAt;
    }
}

// ────────────────────────────────────────────────────────────
// DASHBOARD
// ────────────────────────────────────────────────────────────
public class DashboardDto {
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class GlobalStats {
        private long totalMembres;
        private long membresActifs;
        private long totalDahiras;
        private BigDecimal totalCotisations;
        private BigDecimal cotisationsCeMois;
        private long evenementsEnCours;
        private long projetsActifs;
        private long notificationsNonLues;
        private long nouveauxMembresCeMois;
    }
}
