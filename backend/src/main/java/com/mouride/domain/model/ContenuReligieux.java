package com.mouride.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contenus_religieux")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ContenuReligieux {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private Type type;

    @Column(nullable = false)
    private String titre;

    private String description;

    @Column(columnDefinition = "text")
    private String contenu;

    @Column(name = "url_fichier")
    private String urlFichier;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    private String auteur;
    private String langue = "fr";

    @Column(name = "duree_secondes")
    private Integer dureeSecondes;

    @Column(name = "nb_vues")
    private Integer nbVues = 0;

    @Column(name = "est_publie")
    private boolean estPublie = false;

    @Column(name = "publie_par")
    private UUID publieParId;

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

    public enum Type { KHASSAIDE, ARTICLE, AUDIO, VIDEO, PDF, CITATION }
}
