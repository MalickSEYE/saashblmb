package com.mouride.domain.repository;

import com.mouride.domain.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
interface ContenuReligieuxRepository extends JpaRepository<ContenuReligieux, UUID> {
    Page<ContenuReligieux> findByEstPublieTrue(Pageable pageable);
    Page<ContenuReligieux> findByType(ContenuReligieux.Type type, Pageable pageable);
    Page<ContenuReligieux> findByTypeAndEstPublieTrue(ContenuReligieux.Type type, Pageable pageable);
    @Query("SELECT c FROM ContenuReligieux c WHERE LOWER(c.titre) LIKE LOWER(CONCAT('%',:q,'%'))")
    Page<ContenuReligieux> search(@Param("q") String q, Pageable pageable);
}

@Repository
interface ProjetSocialRepository extends JpaRepository<ProjetSocial, UUID> {
    List<ProjetSocial> findByStatut(ProjetSocial.Statut statut);
    Page<ProjetSocial> findAllByOrderByCreatedAtDesc(Pageable pageable);
}

@Repository
interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    Page<AuditLog> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    Page<AuditLog> findByEntiteOrderByCreatedAtDesc(String entite, Pageable pageable);
}
