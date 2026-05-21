package com.mouride.controller;

import com.mouride.application.dto.CotisationDtos;
import com.mouride.application.usecase.CotisationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.mouride.domain.model.Cotisation;
import com.mouride.domain.model.User;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cotisations")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Cotisations", description = "Gestion des cotisations et paiements")
public class CotisationController {

    private final CotisationService cotisationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','RESPONSABLE')")
    @Operation(summary = "Liste des cotisations")
    public ResponseEntity<Page<CotisationDtos.Response>> lister(
            @RequestParam(required = false) Cotisation.Statut statut,
            @RequestParam(required = false) UUID dahiraId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(cotisationService.lister(dahiraId, statut,
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "datePaiement"))));
    }

    @PostMapping
    @Operation(summary = "Enregistrer une cotisation")
    public ResponseEntity<CotisationDtos.Response> enregistrer(
            @Valid @RequestBody CotisationDtos.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cotisationService.enregistrer(request));
    }

    @PutMapping("/{id}/valider")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Valider un paiement")
    public ResponseEntity<CotisationDtos.Response> valider(@PathVariable UUID id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(cotisationService.valider(id, user.getId()));
    }

    @PutMapping("/{id}/rejeter")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Rejeter un paiement")
    public ResponseEntity<CotisationDtos.Response> rejeter(@PathVariable UUID id,
            @RequestBody(required = false) Map<String, String> body) {
        String raison = body != null ? body.get("raison") : null;
        return ResponseEntity.ok(cotisationService.rejeter(id, raison));
    }

    @GetMapping("/membre/{membreId}")
    @Operation(summary = "Cotisations d'un membre")
    public ResponseEntity<List<CotisationDtos.Response>> parMembre(@PathVariable UUID membreId) {
        return ResponseEntity.ok(cotisationService.parMembre(membreId));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Statistiques financières")
    public ResponseEntity<CotisationDtos.FinanceStats> stats() {
        return ResponseEntity.ok(cotisationService.getStats());
    }
}
