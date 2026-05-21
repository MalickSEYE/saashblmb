package com.mouride.application.dto;

import com.mouride.domain.model.Membre;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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
