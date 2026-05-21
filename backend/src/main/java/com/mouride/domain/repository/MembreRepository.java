package com.mouride.domain.repository;

import com.mouride.domain.model.Membre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MembreRepository extends JpaRepository<Membre, UUID> {
    Page<Membre> findByDahiraId(UUID dahiraId, Pageable pageable);
    Page<Membre> findByStatut(Membre.Statut statut, Pageable pageable);
    Optional<Membre> findByNumeroMembre(String numeroMembre);
    boolean existsByEmail(String email);
    long countByStatut(Membre.Statut statut);
    long countByDahiraId(UUID dahiraId);

    @Query("SELECT m FROM Membre m WHERE " +
           "LOWER(m.nom) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(m.prenom) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(m.email) LIKE LOWER(CONCAT('%',:q,'%'))")
    Page<Membre> search(@Param("q") String query, Pageable pageable);
}
