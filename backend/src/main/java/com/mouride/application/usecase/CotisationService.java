package com.mouride.application.usecase;

import com.mouride.application.dto.CotisationDtos.*;
import com.mouride.domain.model.Cotisation;
import com.mouride.domain.repository.CotisationRepository;
import com.mouride.domain.repository.DahiraRepository;
import com.mouride.domain.repository.MembreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CotisationService {

    private final CotisationRepository cotisationRepository;
    private final MembreRepository membreRepository;
    private final DahiraRepository dahiraRepository;

    public Response enregistrer(Request req) {
        Cotisation c = Cotisation.builder()
            .membreId(req.getMembreId())
            .dahiraId(req.getDahiraId())
            .montant(req.getMontant())
            .type(req.getType() != null ? req.getType() : Cotisation.Type.MENSUELLE)
            .periode(req.getPeriode())
            .statut(Cotisation.Statut.EN_ATTENTE)
            .moyenPaiement(req.getMoyenPaiement() != null ? req.getMoyenPaiement() : Cotisation.MoyenPaiement.MANUEL)
            .referencePaiement(req.getReferencePaiement())
            .notes(req.getNotes())
            .datePaiement(LocalDateTime.now())
            .build();
        return toResponse(cotisationRepository.save(c));
    }

    public Response valider(UUID id, UUID valideurId) {
        Cotisation c = cotisationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cotisation introuvable : " + id));
        if (c.getStatut() != Cotisation.Statut.EN_ATTENTE)
            throw new RuntimeException("Cette cotisation ne peut plus être validée (statut : " + c.getStatut() + ")");
        c.setStatut(Cotisation.Statut.VALIDEE);
        c.setValideeParId(valideurId);
        c.setDateValidation(LocalDateTime.now());
        return toResponse(cotisationRepository.save(c));
    }

    public Response rejeter(UUID id, String raison) {
        Cotisation c = cotisationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cotisation introuvable : " + id));
        c.setStatut(Cotisation.Statut.REJETEE);
        if (raison != null) c.setNotes(raison);
        return toResponse(cotisationRepository.save(c));
    }

    @Transactional(readOnly = true)
    public Page<Response> lister(UUID dahiraId, Cotisation.Statut statut, Pageable pageable) {
        if (dahiraId != null) return cotisationRepository.findByDahiraId(dahiraId, pageable).map(this::toResponse);
        if (statut != null)   return cotisationRepository.findAll(pageable).map(this::toResponse);
        return cotisationRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<Response> parMembre(UUID membreId) {
        return cotisationRepository.findByMembreId(membreId).stream()
            .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FinanceStats getStats() {
        LocalDateTime debutMois   = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime debutAnnee  = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
        return FinanceStats.builder()
            .totalValide(safe(cotisationRepository.sumTotalValide()))
            .totalCeMois(safe(cotisationRepository.sumValideDepuis(debutMois)))
            .totalCetteAnnee(safe(cotisationRepository.sumValideDepuis(debutAnnee)))
            .nbEnAttente(cotisationRepository.countByStatut(Cotisation.Statut.EN_ATTENTE))
            .nbValide(cotisationRepository.countByStatut(Cotisation.Statut.VALIDEE))
            .nbRejete(cotisationRepository.countByStatut(Cotisation.Statut.REJETEE))
            .build();
    }

    private Response toResponse(Cotisation c) {
        String membreNom  = membreRepository.findById(c.getMembreId())
            .map(m -> m.getPrenom() + " " + m.getNom()).orElse("Inconnu");
        String dahiraNom  = c.getDahiraId() != null
            ? dahiraRepository.findById(c.getDahiraId()).map(Dahira::getNom).orElse(null)
            : null;
        return Response.builder()
            .id(c.getId()).membreId(c.getMembreId()).membreNomComplet(membreNom)
            .dahiraId(c.getDahiraId()).dahiraNom(dahiraNom)
            .montant(c.getMontant()).type(c.getType()).periode(c.getPeriode())
            .statut(c.getStatut()).moyenPaiement(c.getMoyenPaiement())
            .referencePaiement(c.getReferencePaiement()).notes(c.getNotes())
            .datePaiement(c.getDatePaiement()).dateValidation(c.getDateValidation())
            .build();
    }

    private BigDecimal safe(BigDecimal v) { return v != null ? v : BigDecimal.ZERO; }
}
