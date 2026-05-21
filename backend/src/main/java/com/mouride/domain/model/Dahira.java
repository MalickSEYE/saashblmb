package com.mouride.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dahiras")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Dahira {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nom;

    @Column(unique = true)
    private String code;

    private String description;
    private String ville;

    @Column(nullable = false)
    private String pays = "Sénégal";

    private String adresse;
    private String telephone;
    private String email;

    @Column(name = "responsable_id")
    private UUID responsableId;

    @Column(name = "date_creation")
    private LocalDate dateCreation;

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() { this.updatedAt = LocalDateTime.now(); }
}
