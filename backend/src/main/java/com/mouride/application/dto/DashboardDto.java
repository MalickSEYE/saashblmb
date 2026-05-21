package com.mouride.application.dto;

import lombok.*;
import java.math.BigDecimal;

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
