package com.mouride.domain.repository;

import com.mouride.domain.model.ProjetSocial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ProjetSocialRepository extends JpaRepository<ProjetSocial, UUID> {
}
