package com.mouride.domain.repository;

import com.mouride.domain.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// ── UserRepository ────────────────────────────────────────
@Repository
interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}

// ── DahiraRepository ──────────────────────────────────────
@Repository
interface DahiraRepository extends JpaRepository<Dahira, UUID> {
    List<Dahira> findByActiveTrue();
    Optional<Dahira> findByCode(String code);
    Page<Dahira> findByPaysIgnoreCase(String pays, Pageable pageable);
}

// ── MembreRepository ──────────────────────────────────────
@Repository
interface MembreRepository extends JpaRepository<Membre, UUID> {
    Page<Membre> findByDahiraId(UUID dahiraId, Pageable pageable);
    Page<Membre> findByStatut(Membre.Statut statut, Pageable pageable);
    Optional<Membre> findByNumeroMembre(String numeroMembre);
    boolean existsByEmail(String email);
    long countByStatut(Membre.Statut statut);
    long countByDahiraId(UUID dahiraId);

    @Query("SELECT m FROM Membre m WHERE " +
           "LOWER(m.nom) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(m.prenom) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(m.email) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "m.telephone LIKE CONCAT('%',:q,'%')")
    Page<Membre> search(@Param("q") String query, Pageable pageable);

    @Query("SELECT m FROM Membre m WHERE m.dahiraId = :dahiraId AND m.statut = :statut")
    List<Membre> findByDahiraIdAndStatut(@Param("dahiraId") UUID dahiraId,
                                         @Param("statut") Membre.Statut statut);
}

// ── CotisationRepository ──────────────────────────────────
@Repository
interface CotisationRepository extends JpaRepository<Cotisation, UUID> {
    List<Cotisation> findByMembreId(UUID membreId);
    Page<Cotisation> findByDahiraId(UUID dahiraId, Pageable pageable);
    List<Cotisation> findByStatut(Cotisation.Statut statut);
    List<Cotisation> findByMembreIdAndPeriode(UUID membreId, String periode);

    @Query("SELECT COALESCE(SUM(c.montant), 0) FROM Cotisation c WHERE c.statut = 'VALIDEE'")
    BigDecimal sumTotalValide();

    @Query("SELECT COALESCE(SUM(c.montant), 0) FROM Cotisation c " +
           "WHERE c.statut = 'VALIDEE' AND c.datePaiement >= :depuis")
    BigDecimal sumValideDepuis(@Param("depuis") LocalDateTime depuis);

    @Query("SELECT COALESCE(SUM(c.montant), 0) FROM Cotisation c " +
           "WHERE c.dahiraId = :dahiraId AND c.statut = 'VALIDEE'")
    BigDecimal sumByDahira(@Param("dahiraId") UUID dahiraId);

    long countByStatut(Cotisation.Statut statut);
}

// ── EvenementRepository ───────────────────────────────────
@Repository
interface EvenementRepository extends JpaRepository<Evenement, UUID> {
    Page<Evenement> findByStatutOrderByDateDebutDesc(Evenement.Statut statut, Pageable pageable);
    List<Evenement> findByDateDebutBetween(LocalDateTime debut, LocalDateTime fin);
    Page<Evenement> findByDahiraId(UUID dahiraId, Pageable pageable);
    long countByStatut(Evenement.Statut statut);
}
