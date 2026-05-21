package com.mouride.controller;

import com.mouride.application.usecase.*;
import com.mouride.domain.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

// ── EXPORT CONTROLLER ─────────────────────────────────────
@RestController
@RequestMapping("/api/v1/export")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Export", description = "Export PDF et Excel")
class ExportController {

    private final ExportService exportService;

    @GetMapping("/membres/excel")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Exporter la liste des membres en Excel")
    public ResponseEntity<byte[]> membresExcel() throws Exception {
        byte[] data = exportService.exportMembresExcel();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=membres.xlsx")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(data);
    }

    @GetMapping("/membres/{id}/carte")
    @Operation(summary = "Générer la carte de membre PDF")
    public ResponseEntity<byte[]> carteMembre(@PathVariable UUID id,
            @org.springframework.beans.factory.annotation.Autowired
            com.mouride.domain.repository.MembreRepository repo) throws Exception {
        Membre m = repo.findById(id).orElseThrow(() -> new RuntimeException("Membre introuvable"));
        byte[] data = exportService.genererCarteMembre(m);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=carte-" + m.getNumeroMembre() + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(data);
    }

    @GetMapping("/finance/rapport")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Rapport financier PDF")
    public ResponseEntity<byte[]> rapportFinancier(
            @RequestParam(defaultValue = "2024") String periode) throws Exception {
        byte[] data = exportService.genererRapportFinancier(periode, null);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport-" + periode + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(data);
    }
}

// ── UPLOAD CONTROLLER ─────────────────────────────────────
@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Fichiers", description = "Upload photos et documents")
class UploadController {

    private final com.mouride.infrastructure.storage.StorageService storageService;
    private final com.mouride.domain.repository.MembreRepository membreRepository;

    @PostMapping("/membres/{id}/photo")
    @Operation(summary = "Upload photo de profil d'un membre")
    public ResponseEntity<Map<String, String>> uploadPhoto(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) throws Exception {
        if (!file.getContentType().startsWith("image/"))
            return ResponseEntity.badRequest().body(Map.of("error", "Fichier image requis"));

        String objectName = storageService.uploadFichier(file, "photos");
        String url = storageService.getUrlTemporaire(objectName);

        membreRepository.findById(id).ifPresent(m -> {
            m.setPhotoUrl(objectName);
            membreRepository.save(m);
        });
        return ResponseEntity.ok(Map.of("url", url, "objectName", objectName));
    }

    @PostMapping("/contenus")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Upload fichier contenu religieux")
    public ResponseEntity<Map<String, String>> uploadContenu(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "contenus") String dossier) throws Exception {
        String objectName = storageService.uploadFichier(file, dossier);
        String url = storageService.getUrlTemporaire(objectName);
        return ResponseEntity.ok(Map.of("url", url, "objectName", objectName));
    }
}

// ── NOTIFICATION CONTROLLER ───────────────────────────────
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Notifications", description = "Gestion des notifications et communications")
class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/email")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','RESPONSABLE')")
    @Operation(summary = "Envoyer un email à un utilisateur")
    public ResponseEntity<Map<String, String>> envoyerEmail(@RequestBody Map<String, String> body) {
        notificationService.envoyerEmail(
            UUID.fromString(body.get("destinataireId")),
            body.get("sujet"), body.get("message"));
        return ResponseEntity.ok(Map.of("status", "envoyé"));
    }

    @PostMapping("/diffusion")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Diffusion à tous les membres")
    public ResponseEntity<Map<String, String>> diffuser(@RequestBody Map<String, String> body) {
        Notification.Canal canal = Notification.Canal.valueOf(
            body.getOrDefault("canal", "EMAIL"));
        notificationService.diffuserATous(body.get("sujet"), body.get("message"), canal);
        return ResponseEntity.ok(Map.of("status", "diffusé"));
    }

    @GetMapping("/non-lues")
    @Operation(summary = "Notifications non lues de l'utilisateur connecté")
    public ResponseEntity<?> nonLues(
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            com.mouride.domain.model.User user) {
        return ResponseEntity.ok(notificationService.getNonLues(user.getId()));
    }

    @PutMapping("/{id}/lire")
    @Operation(summary = "Marquer une notification comme lue")
    public ResponseEntity<Void> marquerLu(@PathVariable UUID id,
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            com.mouride.domain.model.User user) {
        notificationService.marquerLu(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}

// ── EVENEMENT INSCRIPTION CONTROLLER ─────────────────────
@RestController
@RequestMapping("/api/v1/evenements/{evenementId}/inscriptions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Événements", description = "Inscriptions et présences")
class InscriptionController {

    private final EvenementService evenementService;

    @PostMapping
    @Operation(summary = "Inscrire un membre à un événement")
    public ResponseEntity<Map<String, String>> inscrire(
            @PathVariable UUID evenementId,
            @RequestBody Map<String, String> body) {
        String qrBase64 = evenementService.inscrireMembre(
            evenementId, UUID.fromString(body.get("membreId")));
        return ResponseEntity.ok(Map.of("qrCode", qrBase64, "message", "Inscription confirmée"));
    }

    @PostMapping("/presence")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','RESPONSABLE')")
    @Operation(summary = "Scanner QR code et marquer présent")
    public ResponseEntity<Map<String, String>> marquerPresent(@RequestBody Map<String, String> body) {
        evenementService.marquerPresent(body.get("qrCode"));
        return ResponseEntity.ok(Map.of("status", "Présence enregistrée"));
    }
}

// ── PROJETS SOCIAUX CONTROLLER ────────────────────────────
@RestController
@RequestMapping("/api/v1/projets")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Projets sociaux", description = "Projets humanitaires et communautaires")
class ProjetController {

    private final ProjetService projetService;

    @GetMapping
    @Operation(summary = "Liste des projets sociaux")
    public ResponseEntity<Page<ProjetSocial>> lister(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(projetService.lister(
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un projet")
    public ResponseEntity<ProjetSocial> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(projetService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Créer un projet social")
    public ResponseEntity<ProjetSocial> creer(@RequestBody ProjetSocial projet) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projetService.creer(projet));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Modifier un projet")
    public ResponseEntity<ProjetSocial> modifier(@PathVariable UUID id, @RequestBody ProjetSocial projet) {
        return ResponseEntity.ok(projetService.modifier(id, projet));
    }
}

// ── CONTENUS RELIGIEUX CONTROLLER ────────────────────────
@RestController
@RequestMapping("/api/v1/contenus")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Contenus religieux", description = "Khassaïdes, articles, audios, vidéos, PDF")
class ContenuController {

    private final ContenuService contenuService;

    @GetMapping
    @Operation(summary = "Liste des contenus")
    public ResponseEntity<Page<ContenuReligieux>> lister(
            @RequestParam(required = false) ContenuReligieux.Type type,
            @RequestParam(defaultValue = "true") boolean publieOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(contenuService.lister(type, publieOnly,
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))));
    }

    @GetMapping("/public")
    @Operation(summary = "Contenus publics (sans authentification)")
    public ResponseEntity<Page<ContenuReligieux>> publics(
            @RequestParam(required = false) ContenuReligieux.Type type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(contenuService.lister(type, true,
            PageRequest.of(page, size)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Créer un contenu religieux")
    public ResponseEntity<ContenuReligieux> creer(@RequestBody ContenuReligieux contenu,
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            com.mouride.domain.model.User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contenuService.creer(contenu));
    }

    @PutMapping("/{id}/publier")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Publier un contenu")
    public ResponseEntity<ContenuReligieux> publier(@PathVariable UUID id,
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            com.mouride.domain.model.User user) {
        return contenuService.lister(null, false, PageRequest.of(0, 1))
            .stream().filter(c -> c.getId().equals(id)).findFirst()
            .map(c -> ResponseEntity.ok(contenuService.publier(c, user.getId())))
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(summary = "Supprimer un contenu")
    public ResponseEntity<Void> supprimer(@PathVariable UUID id) {
        contenuService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
