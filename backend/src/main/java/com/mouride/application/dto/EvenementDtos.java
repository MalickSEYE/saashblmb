package com.mouride.application.dto;

import com.mouride.domain.model.Evenement;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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
