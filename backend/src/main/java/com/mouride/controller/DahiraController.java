package com.mouride.controller;

import com.mouride.application.dto.DahiraDtos;
import com.mouride.application.usecase.DahiraService;
import com.mouride.domain.model.Membre;
import com.mouride.domain.repository.MembreRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dahiras")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Dahiras", description = "Gestion des Dahiras")
public class DahiraController {

    private final DahiraService dahiraService;
    private final MembreRepository membreRepository;

    @GetMapping
    @Operation(summary = "Liste tous les Dahiras actifs")
    public ResponseEntity<List<DahiraDtos.Response>> lister() {
        return ResponseEntity.ok(dahiraService.listerActifs());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un Dahira")
    public ResponseEntity<DahiraDtos.Response> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(dahiraService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Créer un Dahira")
    public ResponseEntity<DahiraDtos.Response> creer(@RequestBody DahiraDtos.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dahiraService.creer(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','RESPONSABLE')")
    @Operation(summary = "Modifier un Dahira")
    public ResponseEntity<DahiraDtos.Response> modifier(@PathVariable UUID id,
                                                         @RequestBody DahiraDtos.Request request) {
        return ResponseEntity.ok(dahiraService.modifier(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Désactiver un Dahira")
    public ResponseEntity<Void> supprimer(@PathVariable UUID id) {
        dahiraService.desactiver(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/membres")
    @Operation(summary = "Membres du Dahira")
    public ResponseEntity<Page<Membre>> membres(@PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(membreRepository.findByDahiraId(id, PageRequest.of(page, size)));
    }
}
