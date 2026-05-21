package com.mouride.application.dto;

import com.mouride.domain.model.Cotisation;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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
