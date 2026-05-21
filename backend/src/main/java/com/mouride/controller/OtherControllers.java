package com.mouride.controller;

import com.mouride.application.dto.*;
import com.mouride.domain.model.*;
import com.mouride.domain.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// ── DAHIRA CONTROLLER ─────────────────────────────────────
@RestController
@RequestMapping("/api/v1/dahiras")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Dahiras", description = "Gestion des Dahiras")
class DahiraController {

    private final DahiraRepository dahiraRepository;
    private final MembreRepository membreRepository;
    private final CotisationRepository cotisationRepository;

    @GetMapping
    @Operation(summary = "Liste tous les Dahiras")
    public ResponseEntity<List<Dahira>> lister() {
        return ResponseEntity.ok(dahiraRepository.findByActiveTrue());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un Dahira")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        return dahiraRepository.findById(id)
            .map(d -> {
                long nbMembres = membreRepository.countByDahiraId(id);
                BigDecimal total = cotisationRepository.sumByDahira(id);
                var resp = DahiraDtos.Response.builder()
                    .id(d.getId()).nom(d.getNom()).code(d.getCode())
                    .description(d.getDescription()).ville(d.getVille()).pays(d.getPays())
                    .adresse(d.getAdresse()).telephone(d.getTelephone()).email(d.getEmail())
                    .responsableId(d.getResponsableId()).dateCreation(d.getDateCreation())
                    .active(d.isActive()).nombreMembres(nbMembres)
                    .totalCotisations(total != null ? total : BigDecimal.ZERO)
                    .build();
                return ResponseEntity.ok(resp);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Créer un Dahira")
    public ResponseEntity<Dahira> creer(@Valid @RequestBody DahiraDtos.Request request) {
        Dahira d = Dahira.builder()
            .nom(request.getNom()).code(request.getCode())
            .description(request.getDescription())
            .ville(request.getVille()).pays(request.getPays() != null ? request.getPays() : "Sénégal")
            .adresse(request.getAdresse()).telephone(request.getTelephone())
            .email(request.getEmail()).responsableId(request.getResponsableId())
            .dateCreation(request.getDateCreation()).active(true)
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(dahiraRepository.save(d));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','RESPONSABLE')")
    @Operation(summary = "Modifier un Dahira")
    public ResponseEntity<?> modifier(@PathVariable UUID id, @Valid @RequestBody DahiraDtos.Request req) {
        return dahiraRepository.findById(id).map(d -> {
            d.setNom(req.getNom()); d.setDescription(req.getDescription());
            d.setVille(req.getVille()); d.setPays(req.getPays());
            d.setAdresse(req.getAdresse()); d.setTelephone(req.getTelephone());
            d.setEmail(req.getEmail()); d.setResponsableId(req.getResponsableId());
            return ResponseEntity.ok(dahiraRepository.save(d));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/membres")
    @Operation(summary = "Membres d'un Dahira")
    public ResponseEntity<Page<Membre>> membres(@PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(membreRepository.findByDahiraId(id, PageRequest.of(page, size)));
    }
}

// ── COTISATION CONTROLLER ─────────────────────────────────
@RestController
@RequestMapping("/api/v1/cotisations")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Cotisations", description = "Gestion des cotisations et paiements")
class CotisationController {

    private final CotisationRepository cotisationRepository;
    private final MembreRepository membreRepository;

    @GetMapping
    @Operation(summary = "Liste des cotisations")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','RESPONSABLE')")
    public ResponseEntity<Page<Cotisation>> lister(
            @RequestParam(required = false) Cotisation.Statut statut,
            @RequestParam(required = false) UUID dahiraId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "datePaiement"));
        if (dahiraId != null) return ResponseEntity.ok(cotisationRepository.findByDahiraId(dahiraId, pageable));
        return ResponseEntity.ok(cotisationRepository.findAll(pageable));
    }

    @PostMapping
    @Operation(summary = "Enregistrer une cotisation")
    public ResponseEntity<Cotisation> creer(@Valid @RequestBody CotisationDtos.Request req) {
        Cotisation c = Cotisation.builder()
            .membreId(req.getMembreId()).dahiraId(req.getDahiraId())
            .montant(req.getMontant())
            .type(req.getType() != null ? req.getType() : Cotisation.Type.MENSUELLE)
            .periode(req.getPeriode())
            .statut(Cotisation.Statut.EN_ATTENTE)
            .moyenPaiement(req.getMoyenPaiement() != null ? req.getMoyenPaiement() : Cotisation.MoyenPaiement.MANUEL)
            .referencePaiement(req.getReferencePaiement())
            .notes(req.getNotes())
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(cotisationRepository.save(c));
    }

    @PutMapping("/{id}/valider")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Valider un paiement")
    public ResponseEntity<?> valider(@PathVariable UUID id) {
        return cotisationRepository.findById(id).map(c -> {
            c.setStatut(Cotisation.Statut.VALIDEE);
            c.setDateValidation(LocalDateTime.now());
            return ResponseEntity.ok(cotisationRepository.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/rejeter")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Rejeter un paiement")
    public ResponseEntity<?> rejeter(@PathVariable UUID id,
                                     @RequestBody(required = false) Map<String, String> body) {
        return cotisationRepository.findById(id).map(c -> {
            c.setStatut(Cotisation.Statut.REJETEE);
            if (body != null && body.containsKey("raison")) c.setNotes(body.get("raison"));
            return ResponseEntity.ok(cotisationRepository.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/membre/{membreId}")
    @Operation(summary = "Cotisations d'un membre")
    public ResponseEntity<List<Cotisation>> parMembre(@PathVariable UUID membreId) {
        return ResponseEntity.ok(cotisationRepository.findByMembreId(membreId));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Statistiques financières")
    public ResponseEntity<CotisationDtos.FinanceStats> stats() {
        LocalDateTime debutMois = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        return ResponseEntity.ok(CotisationDtos.FinanceStats.builder()
            .totalValide(cotisationRepository.sumTotalValide())
            .totalCeMois(cotisationRepository.sumValideDepuis(debutMois))
            .nbEnAttente(cotisationRepository.countByStatut(Cotisation.Statut.EN_ATTENTE))
            .nbValide(cotisationRepository.countByStatut(Cotisation.Statut.VALIDEE))
            .nbRejete(cotisationRepository.countByStatut(Cotisation.Statut.REJETEE))
            .build());
    }
}

// ── EVENEMENT CONTROLLER ──────────────────────────────────
@RestController
@RequestMapping("/api/v1/evenements")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Événements", description = "Gestion des événements religieux")
class EvenementController {

    private final EvenementRepository evenementRepository;

    @GetMapping
    @Operation(summary = "Liste des événements")
    public ResponseEntity<Page<Evenement>> lister(
            @RequestParam(required = false) Evenement.Statut statut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateDebut"));
        if (statut != null) return ResponseEntity.ok(
            evenementRepository.findByStatutOrderByDateDebutDesc(statut, pageable));
        return ResponseEntity.ok(evenementRepository.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un événement")
    public ResponseEntity<Evenement> getById(@PathVariable UUID id) {
        return evenementRepository.findById(id).map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','RESPONSABLE')")
    @Operation(summary = "Créer un événement")
    public ResponseEntity<Evenement> creer(@Valid @RequestBody EvenementDtos.Request req) {
        Evenement e = Evenement.builder()
            .titre(req.getTitre())
            .type(req.getType() != null ? req.getType() : Evenement.Type.REUNION)
            .description(req.getDescription())
            .dateDebut(req.getDateDebut()).dateFin(req.getDateFin())
            .lieu(req.getLieu()).capaciteMax(req.getCapaciteMax())
            .prixEntree(req.getPrixEntree() != null ? req.getPrixEntree() : BigDecimal.ZERO)
            .dahiraId(req.getDahiraId())
            .statut(Evenement.Statut.PLANIFIE)
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(evenementRepository.save(e));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','RESPONSABLE')")
    @Operation(summary = "Modifier un événement")
    public ResponseEntity<?> modifier(@PathVariable UUID id, @Valid @RequestBody EvenementDtos.Request req) {
        return evenementRepository.findById(id).map(e -> {
            e.setTitre(req.getTitre()); e.setDescription(req.getDescription());
            e.setDateDebut(req.getDateDebut()); e.setDateFin(req.getDateFin());
            e.setLieu(req.getLieu()); e.setCapaciteMax(req.getCapaciteMax());
            return ResponseEntity.ok(evenementRepository.save(e));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Annuler un événement")
    public ResponseEntity<Void> annuler(@PathVariable UUID id) {
        evenementRepository.findById(id).ifPresent(e -> {
            e.setStatut(Evenement.Statut.ANNULE);
            evenementRepository.save(e);
        });
        return ResponseEntity.noContent().build();
    }
}

// ── DASHBOARD CONTROLLER ──────────────────────────────────
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Dashboard", description = "KPIs et statistiques globales")
class DashboardController {

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
            .nouveauxMembresCeMois(0L) // TODO: implémenter
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
