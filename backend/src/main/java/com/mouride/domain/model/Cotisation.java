package com.mouride.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cotisations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cotisation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "membre_id", nullable = false)
    private UUID membreId;

    @Column(name = "dahira_id")
    private UUID dahiraId;

    @Column(nullable = false)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "cotisation_type", nullable = false)
    private Type type = Type.MENSUELLE;

    private String periode; // YYYY-MM

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "cotisation_statut", nullable = false)
    private Statut statut = Statut.EN_ATTENTE;

    @Enumerated(EnumType.STRING)
    @Column(name = "moyen_paiement", columnDefinition = "paiement_moyen", nullable = false)
    private MoyenPaiement moyenPaiement = MoyenPaiement.MANUEL;

    @Column(name = "reference_paiement")
    private String referencePaiement;

    private String notes;

    @Column(name = "validee_par")
    private UUID valideeParId;

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @Column(name = "date_paiement", nullable = false)
    private LocalDateTime datePaiement = LocalDateTime.now();

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Type    { MENSUELLE, ANNUELLE, SPECIALE, DON }
    public enum Statut  { EN_ATTENTE, VALIDEE, REJETEE }
    public enum MoyenPaiement { WAVE, ORANGE_MONEY, FREE_MONEY, CARTE, MANUEL }
}
