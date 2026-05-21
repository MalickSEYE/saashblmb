package com.mouride.domain.repository;

import com.mouride.domain.model.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, UUID> {
    boolean existsByMembreIdAndEvenementId(UUID membreId, UUID evenementId);
    Optional<Participation> findByQrCode(String qrCode);
    long countByEvenementId(UUID evenementId);
    long countByEvenementIdAndPresentTrue(UUID evenementId);
}
