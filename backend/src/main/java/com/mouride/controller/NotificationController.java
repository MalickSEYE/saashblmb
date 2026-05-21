package com.mouride.controller;

import com.mouride.application.usecase.NotificationService;
import com.mouride.domain.model.Notification;
import com.mouride.domain.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Notifications", description = "Gestion des notifications in-app")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/in-app")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','RESPONSABLE')")
    @Operation(summary = "Envoyer une notification in-app")
    public ResponseEntity<Map<String, String>> envoyer(@RequestBody Map<String, String> body) {
        notificationService.envoyerInApp(
            UUID.fromString(body.get("destinataireId")),
            body.get("sujet"),
            body.get("message")
        );
        return ResponseEntity.ok(Map.of("status", "envoyé"));
    }

    @GetMapping("/non-lues")
    @Operation(summary = "Notifications non lues de l'utilisateur connecté")
    public ResponseEntity<List<Notification>> nonLues(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(notificationService.getNonLues(user.getId()));
    }

    @GetMapping("/non-lues/count")
    @Operation(summary = "Nombre de notifications non lues")
    public ResponseEntity<Map<String, Long>> count(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(Map.of("count", notificationService.countNonLues(user.getId())));
    }

    @PutMapping("/{id}/lire")
    @Operation(summary = "Marquer une notification comme lue")
    public ResponseEntity<Void> marquerLu(@PathVariable UUID id,
            @AuthenticationPrincipal User user) {
        notificationService.marquerLu(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
