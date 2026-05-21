package com.mouride.application.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class DahiraDtos {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        private String nom;
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
