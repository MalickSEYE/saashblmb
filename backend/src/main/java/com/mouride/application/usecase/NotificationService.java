package com.mouride.application.usecase;

import com.mouride.domain.model.Notification;
import com.mouride.domain.repository.NotificationRepository;
import com.mouride.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    private final JavaMailSender mailSender;
    private final RabbitTemplate rabbitTemplate;
    private final UserRepository userRepository;

    public void envoyerEmail(UUID destinataireId, String sujet, String message) {
        Notification notif = Notification.builder()
            .destinataireId(destinataireId)
            .canal(Notification.Canal.EMAIL)
            .sujet(sujet).message(message)
            .statut(Notification.Statut.EN_ATTENTE)
            .build();
        notif = notificationRepository.save(notif);

        try {
            userRepository.findById(destinataireId).ifPresent(user -> {
                SimpleMailMessage mail = new SimpleMailMessage();
                mail.setTo(user.getEmail());
                mail.setSubject("[Mouride SaaS] " + sujet);
                mail.setText(message);
                mailSender.send(mail);
            });
            notif.setStatut(Notification.Statut.ENVOYE);
            notif.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            log.error("Erreur envoi email : {}", e.getMessage());
            notif.setStatut(Notification.Statut.ECHEC);
            notif.setErrorMessage(e.getMessage());
        }
        notificationRepository.save(notif);
    }

    public void envoyerSms(UUID destinataireId, String message) {
        Notification notif = Notification.builder()
            .destinataireId(destinataireId)
            .canal(Notification.Canal.SMS)
            .message(message)
            .statut(Notification.Statut.EN_ATTENTE)
            .build();
        notificationRepository.save(notif);
        // Publier dans la queue RabbitMQ pour traitement asynchrone
        rabbitTemplate.convertAndSend("mouride.notifications", "sms",
            java.util.Map.of("destinataireId", destinataireId, "message", message));
        log.info("SMS en file d'attente pour : {}", destinataireId);
    }

    public void envoyerInApp(UUID destinataireId, String sujet, String message) {
        Notification notif = Notification.builder()
            .destinataireId(destinataireId)
            .canal(Notification.Canal.IN_APP)
            .sujet(sujet).message(message)
            .statut(Notification.Statut.ENVOYE)
            .sentAt(LocalDateTime.now())
            .build();
        notificationRepository.save(notif);
    }

    public void diffuserATous(String sujet, String message, Notification.Canal canal) {
        userRepository.findAll().forEach(user ->
            envoyerEmail(user.getId(), sujet, message));
        log.info("Diffusion '{}' envoyée à {} utilisateurs", sujet,
            notificationRepository.count());
    }

    public long marquerLu(UUID notifId, UUID userId) {
        return notificationRepository.findById(notifId)
            .filter(n -> n.getDestinataire().getId().equals(userId))
            .map(n -> {
                n.setLu(true);
                n.setLuAt(LocalDateTime.now());
                n.setStatut(Notification.Statut.LU);
                notificationRepository.save(n);
                return 1L;
            }).orElse(0L);
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
