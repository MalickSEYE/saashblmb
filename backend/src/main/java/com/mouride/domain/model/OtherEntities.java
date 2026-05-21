package com.mouride.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

// ── ContenuReligieux ──────────────────────────────────────
@Entity
@Table(name = "contenus_religieux")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class ContenuReligieux {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Enumerated(EnumType.STRING) @Column(columnDefinition = "contenu_type", nullable = false)
    private Type type;
    @Column(nullable = false) private String titre;
    private String description;
    @Column(columnDefinition = "text") private String contenu;
    @Column(name = "url_fichier")  private String urlFichier;
    @Column(name = "thumbnail_url") private String thumbnailUrl;
    private String auteur;
    private String langue = "fr";
    @Column(name = "duree_secondes") private Integer dureeSecondes;
    @Column(name = "nb_vues") private Integer nbVues = 0;
    @Column(name = "est_publie") private boolean estPublie = false;
    @Column(name = "publie_par") private UUID publieParId;
    @Column(name = "created_at") private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at") private LocalDateTime updatedAt = LocalDateTime.now();
    @PreUpdate public void preUpdate() { this.updatedAt = LocalDateTime.now(); }
    public enum Type { KHASSAIDE, ARTICLE, AUDIO, VIDEO, PDF, CITATION }
}

// ── ProjetSocial ──────────────────────────────────────────
@Entity
@Table(name = "projets_sociaux")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class ProjetSocial {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private String titre;
    private String description;
    @Column(name = "budget_cible")    private BigDecimal budgetCible = BigDecimal.ZERO;
    @Column(name = "montant_collecte") private BigDecimal montantCollecte = BigDecimal.ZERO;
    @Column(name = "date_debut") private LocalDate dateDebut;
    @Column(name = "date_fin")   private LocalDate dateFin;
    @Enumerated(EnumType.STRING) @Column(columnDefinition = "projet_statut", nullable = false)
    private Statut statut = Statut.PLANIFIE;
    @Column(name = "image_url") private String imageUrl;
    @Column(name = "responsable_id") private UUID responsableId;
    @Column(name = "created_at") private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at") private LocalDateTime updatedAt = LocalDateTime.now();
    @PreUpdate public void preUpdate() { this.updatedAt = LocalDateTime.now(); }
    public enum Statut { EN_COURS, TERMINE, SUSPENDU, PLANIFIE }
}

// ── Notification ──────────────────────────────────────────
@Entity
@Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class Notification {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "destinataire_id", nullable = false) private UUID destinataireId;
    @Enumerated(EnumType.STRING) @Column(columnDefinition = "notif_canal", nullable = false)
    private Canal canal = Canal.IN_APP;
    private String sujet;
    @Column(nullable = false) private String message;
    @Enumerated(EnumType.STRING) @Column(columnDefinition = "notif_statut", nullable = false)
    private Statut statut = Statut.EN_ATTENTE;
    private boolean lu = false;
    @Column(name = "lu_at") private LocalDateTime luAt;
    @Column(name = "sent_at") private LocalDateTime sentAt;
    @Column(name = "error_message") private String errorMessage;
    @Column(name = "created_at") private LocalDateTime createdAt = LocalDateTime.now();
    public enum Canal  { EMAIL, SMS, WHATSAPP, IN_APP }
    public enum Statut { EN_ATTENTE, ENVOYE, ECHEC, LU }
}
