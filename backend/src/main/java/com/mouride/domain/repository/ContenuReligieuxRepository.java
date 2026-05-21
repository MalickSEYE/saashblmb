package com.mouride.domain.repository;

import com.mouride.domain.model.ContenuReligieux;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ContenuReligieuxRepository extends JpaRepository<ContenuReligieux, UUID> {
    Page<ContenuReligieux> findByEstPublieTrue(Pageable pageable);
    Page<ContenuReligieux> findByType(ContenuReligieux.Type type, Pageable pageable);
    Page<ContenuReligieux> findByTypeAndEstPublieTrue(ContenuReligieux.Type type, Pageable pageable);
}
