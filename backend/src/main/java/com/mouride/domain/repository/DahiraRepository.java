package com.mouride.domain.repository;

import com.mouride.domain.model.Dahira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DahiraRepository extends JpaRepository<Dahira, UUID> {
    List<Dahira> findByActiveTrue();
    Optional<Dahira> findByCode(String code);
}
