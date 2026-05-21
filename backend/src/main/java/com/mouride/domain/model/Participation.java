package com.mouride.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "participations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "membre_id", nullable = false)
    private UUID membreId;

    @Column(name = "evenement_id", nullable = false)
    private UUID evenementId;

    @Column(name = "qr_code", unique = true)
    private String qrCode;

    private boolean present = false;

    @Column(name = "checked_at")
    private LocalDateTime checkedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
