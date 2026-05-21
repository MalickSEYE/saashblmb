package com.mouride.domain.repository;

import com.mouride.domain.model.Evenement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface EvenementRepository extends JpaRepository<Evenement, UUID> {
    Page<Evenement> findByStatutOrderByDateDebutDesc(Evenement.Statut statut, Pageable pageable);
    Page<Evenement> findByDahiraId(UUID dahiraId, Pageable pageable);
    long countByStatut(Evenement.Statut statut);
}
