package com.mouride.controller;

import com.mouride.application.dto.EvenementDtos;
import com.mouride.application.usecase.EvenementService;
import com.mouride.domain.model.Evenement;
import com.mouride.domain.model.User;
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

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/evenements")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Événements", description = "Gestion des événements religieux")
public class EvenementController {

    private final EvenementService evenementService;

    @GetMapping
    @Operation(summary = "Liste des événements")
    public ResponseEntity<Page<EvenementDtos.Response>> lister(
            @RequestParam(required = false) Evenement.Statut statut,
            @RequestParam(required = false) UUID dahiraId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(evenementService.lister(statut, dahiraId,
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateDebut"))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un événement")
    public ResponseEntity<EvenementDtos.Response> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(evenementService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','RESPONSABLE')")
    @Operation(summary = "Créer un événement")
    public ResponseEntity<EvenementDtos.Response> creer(
            @Valid @RequestBody EvenementDtos.Request request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(evenementService.creer(request, user.getId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','RESPONSABLE')")
    @Operation(summary = "Modifier un événement")
    public ResponseEntity<EvenementDtos.Response> modifier(@PathVariable UUID id,
            @Valid @RequestBody EvenementDtos.Request request) {
        return ResponseEntity.ok(evenementService.modifier(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Annuler un événement")
    public ResponseEntity<Void> annuler(@PathVariable UUID id) {
        evenementService.changerStatut(id, Evenement.Statut.ANNULE);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{evenementId}/inscriptions")
    @Operation(summary = "Inscrire un membre à un événement")
    public ResponseEntity<Map<String, String>> inscrire(@PathVariable UUID evenementId,
            @RequestBody Map<String, String> body) {
        String qrBase64 = evenementService.inscrireMembre(
            evenementId, UUID.fromString(body.get("membreId")));
        return ResponseEntity.ok(Map.of("qrCode", qrBase64, "message", "Inscription confirmée"));
    }

    @PostMapping("/{evenementId}/presence")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','RESPONSABLE')")
    @Operation(summary = "Scanner QR code et marquer présent")
    public ResponseEntity<Map<String, String>> marquerPresent(
            @PathVariable UUID evenementId,
            @RequestBody Map<String, String> body) {
        evenementService.marquerPresent(body.get("qrCode"));
        return ResponseEntity.ok(Map.of("status", "Présence enregistrée"));
    }
}
