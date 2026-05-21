package com.mouride.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "membres")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Membre {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "numero_membre", nullable = false, unique = true)
    private String numeroMembre;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    private String telephone;
    private String email;
    private String adresse;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Sexe sexe;

    private String ville;
    private String pays = "Sénégal";
    private String profession;

    @Column(name = "fonction_religieuse")
    private String fonctionReligieuse;

    @Column(name = "dahira_id")
    private UUID dahiraId;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private Statut statut = Statut.EN_ATTENTE;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "date_adhesion", nullable = false)
    private LocalDate dateAdhesion = LocalDate.now();

    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) this.createdAt = java.time.LocalDateTime.now();
        if (this.updatedAt == null) this.updatedAt = java.time.LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() { this.updatedAt = LocalDateTime.now(); }

    public enum Statut { ACTIF, INACTIF, SUSPENDU, EN_ATTENTE }
    public enum Sexe   { MASCULIN, FEMININ, AUTRE }
}
