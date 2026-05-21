package com.mouride.controller;

import com.mouride.application.dto.MembreDtos.*;
import com.mouride.application.usecase.MembreService;
import com.mouride.domain.model.Membre;
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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/membres")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Membres", description = "Gestion des membres et talibés")
public class MembreController {

    private final MembreService membreService;

    @GetMapping
    @Operation(summary = "Liste des membres (paginée, filtrable)")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','RESPONSABLE')")
    public ResponseEntity<Page<Response>> lister(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID dahiraId,
            @RequestParam(required = false) Membre.Statut statut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "nom") String sort) {
        return ResponseEntity.ok(membreService.lister(search, dahiraId, statut,
            PageRequest.of(page, size, Sort.by(sort))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un membre")
    public ResponseEntity<Response> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(membreService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un nouveau membre")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','RESPONSABLE')")
    public ResponseEntity<Response> creer(@Valid @RequestBody Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(membreService.creer(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un membre")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<Response> modifier(@PathVariable UUID id,
                                              @Valid @RequestBody Request request) {
        return ResponseEntity.ok(membreService.modifier(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Désactiver un membre (soft delete)")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable UUID id) {
        membreService.desactiver(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @Operation(summary = "Statistiques globales des membres")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<Stats> stats() {
        return ResponseEntity.ok(membreService.getStats());
    }
}
