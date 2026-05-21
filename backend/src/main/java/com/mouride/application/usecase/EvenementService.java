package com.mouride.application.usecase;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mouride.application.dto.EvenementDtos.*;
import com.mouride.domain.model.Evenement;
import com.mouride.domain.model.Participation;
import com.mouride.domain.repository.EvenementRepository;
import com.mouride.domain.repository.ParticipationRepository;
import com.mouride.domain.repository.DahiraRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EvenementService {

    private final EvenementRepository evenementRepository;
    private final ParticipationRepository participationRepository;
    private final DahiraRepository dahiraRepository;

    public Response creer(Request req, UUID organisateurId) {
        Evenement e = Evenement.builder()
            .titre(req.getTitre())
            .type(req.getType() != null ? req.getType() : Evenement.Type.REUNION)
            .description(req.getDescription())
            .dateDebut(req.getDateDebut()).dateFin(req.getDateFin())
            .lieu(req.getLieu()).capaciteMax(req.getCapaciteMax())
            .prixEntree(req.getPrixEntree() != null ? req.getPrixEntree() : BigDecimal.ZERO)
            .dahiraId(req.getDahiraId())
            .organisateurId(organisateurId)
            .statut(Evenement.Statut.PLANIFIE)
            .build();
        return toResponse(evenementRepository.save(e));
    }

    public Response modifier(UUID id, Request req) {
        Evenement e = evenementRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Événement introuvable"));
        e.setTitre(req.getTitre()); e.setDescription(req.getDescription());
        e.setDateDebut(req.getDateDebut()); e.setDateFin(req.getDateFin());
        e.setLieu(req.getLieu()); e.setCapaciteMax(req.getCapaciteMax());
        if (req.getDahiraId() != null) e.setDahiraId(req.getDahiraId());
        return toResponse(evenementRepository.save(e));
    }

    @Transactional(readOnly = true)
    public Page<Response> lister(Evenement.Statut statut, UUID dahiraId, Pageable pageable) {
        if (dahiraId != null) return evenementRepository.findByDahiraId(dahiraId, pageable).map(this::toResponse);
        if (statut  != null) return evenementRepository.findByStatutOrderByDateDebutDesc(statut, pageable).map(this::toResponse);
        return evenementRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Response findById(UUID id) {
        return evenementRepository.findById(id).map(this::toResponse)
            .orElseThrow(() -> new RuntimeException("Événement introuvable"));
    }

    public void changerStatut(UUID id, Evenement.Statut statut) {
        evenementRepository.findById(id).ifPresent(e -> {
            e.setStatut(statut);
            evenementRepository.save(e);
        });
    }

    // ── Inscriptions & QR code ──────────────────────────────
    public String inscrireMembre(UUID evenementId, UUID membreId) {
        if (participationRepository.existsByMembreIdAndEvenementId(membreId, evenementId))
            throw new RuntimeException("Ce membre est déjà inscrit à cet événement");

        String qrData = "MOURIDE-EVT:" + evenementId + ":MBR:" + membreId + ":" + System.currentTimeMillis();
        Participation p = Participation.builder()
            .membreId(membreId).evenementId(evenementId)
            .qrCode(qrData).present(false)
            .build();
        participationRepository.save(p);
        return genererQrCodeBase64(qrData);
    }

    public void marquerPresent(String qrCode) {
        Participation p = participationRepository.findByQrCode(qrCode)
            .orElseThrow(() -> new RuntimeException("QR code invalide"));
        p.setPresent(true);
        p.setCheckedAt(LocalDateTime.now());
        participationRepository.save(p);
    }

    private String genererQrCodeBase64(String data) {
        try {
            QRCodeWriter qrWriter = new QRCodeWriter();
            BitMatrix matrix = qrWriter.encode(data, BarcodeFormat.QR_CODE, 300, 300);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            log.error("Erreur génération QR code", e);
            return "";
        }
    }

    private Response toResponse(Evenement e) {
        String dahiraNom = e.getDahiraId() != null
            ? dahiraRepository.findById(e.getDahiraId()).map(d -> d.getNom()).orElse(null)
            : null;
        long nbInscrits = participationRepository.countByEvenementId(e.getId());
        long nbPresents = participationRepository.countByEvenementIdAndPresentTrue(e.getId());
        return Response.builder()
            .id(e.getId()).titre(e.getTitre()).type(e.getType())
            .description(e.getDescription())
            .dateDebut(e.getDateDebut()).dateFin(e.getDateFin())
            .lieu(e.getLieu()).capaciteMax(e.getCapaciteMax())
            .prixEntree(e.getPrixEntree()).imageUrl(e.getImageUrl())
            .dahiraId(e.getDahiraId()).dahiraNom(dahiraNom)
            .statut(e.getStatut()).nbInscrits(nbInscrits).nbPresents(nbPresents)
            .createdAt(e.getCreatedAt())
            .build();
    }
}
