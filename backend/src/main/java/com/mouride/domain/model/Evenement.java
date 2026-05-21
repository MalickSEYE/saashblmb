package com.mouride.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "evenements")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String titre;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private Type type = Type.REUNION;

    private String description;

    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    private String lieu;

    @Column(name = "capacite_max")
    private Integer capaciteMax;

    @Column(name = "prix_entree")
    private BigDecimal prixEntree = BigDecimal.ZERO;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "organisateur_id")
    private UUID organisateurId;

    @Column(name = "dahira_id")
    private UUID dahiraId;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private Statut statut = Statut.PLANIFIE;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() { this.updatedAt = LocalDateTime.now(); }

    public enum Type   { MAGAL, GAMOU, ZIAR, CONFERENCE, REUNION, AUTRE }
    public enum Statut { PLANIFIE, EN_COURS, TERMINE, ANNULE }
}
