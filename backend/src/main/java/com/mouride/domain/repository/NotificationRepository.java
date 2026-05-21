package com.mouride.domain.repository;

import com.mouride.domain.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByDestinataireIdAndLuFalse(UUID destinataireId);
    long countByDestinataireIdAndLuFalse(UUID destinataireId);
    Page<Notification> findByDestinataireIdOrderByCreatedAtDesc(UUID destinataireId, Pageable pageable);
}
