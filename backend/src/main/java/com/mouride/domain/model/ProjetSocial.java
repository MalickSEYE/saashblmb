package com.mouride.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "projets_sociaux")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProjetSocial {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "budget_cible")
    private BigDecimal budgetCible = BigDecimal.ZERO;

    @Column(name = "montant_collecte")
    private BigDecimal montantCollecte = BigDecimal.ZERO;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private Statut statut = Statut.PLANIFIE;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "responsable_id")
    private UUID responsableId;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() { this.updatedAt = LocalDateTime.now(); }

    public enum Statut { EN_COURS, TERMINE, SUSPENDU, PLANIFIE }
}
