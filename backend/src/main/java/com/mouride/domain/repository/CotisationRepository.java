package com.mouride.domain.repository;

import com.mouride.domain.model.Cotisation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CotisationRepository extends JpaRepository<Cotisation, UUID> {
    List<Cotisation> findByMembreId(UUID membreId);
    Page<Cotisation> findByDahiraId(UUID dahiraId, Pageable pageable);
    long countByStatut(Cotisation.Statut statut);

    @Query("SELECT COALESCE(SUM(c.montant), 0) FROM Cotisation c WHERE c.statut = 'VALIDEE'")
    BigDecimal sumTotalValide();

    @Query("SELECT COALESCE(SUM(c.montant), 0) FROM Cotisation c WHERE c.statut = 'VALIDEE' AND c.datePaiement >= :depuis")
    BigDecimal sumValideDepuis(@Param("depuis") LocalDateTime depuis);

    @Query("SELECT COALESCE(SUM(c.montant), 0) FROM Cotisation c WHERE c.dahiraId = :dahiraId AND c.statut = 'VALIDEE'")
    BigDecimal sumByDahira(@Param("dahiraId") UUID dahiraId);
}
