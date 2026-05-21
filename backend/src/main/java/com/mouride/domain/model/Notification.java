package com.mouride.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "destinataire_id", nullable = false)
    private UUID destinataireId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id", insertable = false, updatable = false)
    private User destinataire;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private Canal canal = Canal.IN_APP;

    private String sujet;

    @Column(nullable = false, columnDefinition = "text")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private Statut statut = Statut.EN_ATTENTE;

    private boolean lu = false;

    @Column(name = "lu_at")
    private LocalDateTime luAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Canal  { EMAIL, SMS, WHATSAPP, IN_APP }
    public enum Statut { EN_ATTENTE, ENVOYE, ECHEC, LU }
}
