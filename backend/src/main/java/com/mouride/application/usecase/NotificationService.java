package com.mouride.application.usecase;

import com.mouride.domain.model.Notification;
import com.mouride.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void envoyerInApp(UUID destinataireId, String sujet, String message) {
        Notification notif = Notification.builder()
            .destinataireId(destinataireId)
            .canal(Notification.Canal.IN_APP)
            .sujet(sujet)
            .message(message)
            .statut(Notification.Statut.ENVOYE)
            .sentAt(LocalDateTime.now())
            .build();
        notificationRepository.save(notif);
    }

    public void marquerLu(UUID notifId, UUID userId) {
        notificationRepository.findById(notifId).ifPresent(n -> {
            n.setLu(true);
            n.setLuAt(LocalDateTime.now());
            n.setStatut(Notification.Statut.LU);
            notificationRepository.save(n);
        });
    }

    @Transactional(readOnly = true)
    public List<Notification> getNonLues(UUID userId) {
        return notificationRepository.findByDestinataireIdAndLuFalse(userId);
    }

    @Transactional(readOnly = true)
    public long countNonLues(UUID userId) {
        return notificationRepository.countByDestinataireIdAndLuFalse(userId);
    }
}
