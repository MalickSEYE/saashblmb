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
@Tag(name = "Notifications", description = "Gestion des notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/email")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','RESPONSABLE')")
    @Operation(summary = "Envoyer un email")
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
    @Operation(summary = "Notifications non lues")
    public ResponseEntity<List<Notification>> nonLues(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(notificationService.getNonLues(user.getId()));
    }

    @PutMapping("/{id}/lire")
    @Operation(summary = "Marquer comme lue")
    public ResponseEntity<Void> marquerLu(@PathVariable UUID id,
            @AuthenticationPrincipal User user) {
        notificationService.marquerLu(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
