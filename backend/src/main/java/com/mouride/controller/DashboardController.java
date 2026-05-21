package com.mouride.controller;

import com.mouride.application.dto.DashboardDto;
import com.mouride.domain.model.Cotisation;
import com.mouride.domain.model.Evenement;
import com.mouride.domain.model.Membre;
import com.mouride.domain.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Dashboard", description = "KPIs et statistiques globales")
public class DashboardController {

    private final MembreRepository membreRepository;
    private final DahiraRepository dahiraRepository;
    private final CotisationRepository cotisationRepository;
    private final EvenementRepository evenementRepository;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Statistiques globales du tableau de bord")
    public ResponseEntity<DashboardDto.GlobalStats> stats() {
        LocalDateTime debutMois = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        return ResponseEntity.ok(DashboardDto.GlobalStats.builder()
            .totalMembres(membreRepository.count())
            .membresActifs(membreRepository.countByStatut(Membre.Statut.ACTIF))
            .totalDahiras(dahiraRepository.count())
            .totalCotisations(cotisationRepository.sumTotalValide())
            .cotisationsCeMois(cotisationRepository.sumValideDepuis(debutMois))
            .evenementsEnCours(evenementRepository.countByStatut(Evenement.Statut.EN_COURS))
            .nouveauxMembresCeMois(0L)
            .build());
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Mouride SaaS API",
            "version", "1.0.0",
            "timestamp", LocalDateTime.now().toString()
        ));
    }
}
