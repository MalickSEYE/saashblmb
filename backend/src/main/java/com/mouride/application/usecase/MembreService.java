package com.mouride.application.usecase;

import com.mouride.application.dto.MembreDtos.*;
import com.mouride.domain.model.Membre;
import com.mouride.domain.repository.DahiraRepository;
import com.mouride.domain.repository.MembreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Transactional
public class MembreService {

    private final MembreRepository membreRepository;
    private final DahiraRepository dahiraRepository;

    private static final AtomicLong COUNTER = new AtomicLong(1000);

    public Response creer(Request req) {
        Membre m = new Membre();
        mapRequest(req, m);
        m.setNumeroMembre(genererNumero());
        return toResponse(membreRepository.save(m));
    }

    public Response modifier(UUID id, Request req) {
        Membre m = membreRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Membre introuvable : " + id));
        mapRequest(req, m);
        return toResponse(membreRepository.save(m));
    }

    public void desactiver(UUID id) {
        Membre m = membreRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Membre introuvable : " + id));
        m.setStatut(Membre.Statut.INACTIF);
        membreRepository.save(m);
    }

    @Transactional(readOnly = true)
    public Response findById(UUID id) {
        return membreRepository.findById(id).map(this::toResponse)
            .orElseThrow(() -> new RuntimeException("Membre introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public Page<Response> lister(String search, UUID dahiraId, Membre.Statut statut, Pageable pageable) {
        if (search != null && !search.isBlank())
            return membreRepository.search(search, pageable).map(this::toResponse);
        if (dahiraId != null)
            return membreRepository.findByDahiraId(dahiraId, pageable).map(this::toResponse);
        if (statut != null)
            return membreRepository.findByStatut(statut, pageable).map(this::toResponse);
        return membreRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Stats getStats() {
        long total   = membreRepository.count();
        long actifs  = membreRepository.countByStatut(Membre.Statut.ACTIF);
        long inactifs= membreRepository.countByStatut(Membre.Statut.INACTIF);
        long attente = membreRepository.countByStatut(Membre.Statut.EN_ATTENTE);
        long suspendus=membreRepository.countByStatut(Membre.Statut.SUSPENDU);
        return Stats.builder()
            .total(total).actifs(actifs).inactifs(inactifs)
            .enAttente(attente).suspendus(suspendus)
            .build();
    }

    private void mapRequest(Request req, Membre m) {
        m.setNom(req.getNom());
        m.setPrenom(req.getPrenom());
        m.setEmail(req.getEmail());
        m.setTelephone(req.getTelephone());
        m.setAdresse(req.getAdresse());
        m.setDateNaissance(req.getDateNaissance());
        m.setSexe(req.getSexe());
        m.setVille(req.getVille());
        m.setPays(req.getPays() != null ? req.getPays() : "Sénégal");
        m.setProfession(req.getProfession());
        m.setFonctionReligieuse(req.getFonctionReligieuse());
        m.setDahiraId(req.getDahiraId());
        if (req.getStatut() != null) m.setStatut(req.getStatut());
        m.setNotes(req.getNotes());
    }

    private Response toResponse(Membre m) {
        String dahiraNom = m.getDahiraId() != null
            ? dahiraRepository.findById(m.getDahiraId()).map(d -> d.getNom()).orElse(null)
            : null;
        return Response.builder()
            .id(m.getId())
            .numeroMembre(m.getNumeroMembre())
            .nom(m.getNom()).prenom(m.getPrenom())
            .email(m.getEmail()).telephone(m.getTelephone())
            .adresse(m.getAdresse()).dateNaissance(m.getDateNaissance())
            .sexe(m.getSexe()).ville(m.getVille()).pays(m.getPays())
            .profession(m.getProfession()).fonctionReligieuse(m.getFonctionReligieuse())
            .dahiraId(m.getDahiraId()).dahiraNom(dahiraNom)
            .statut(m.getStatut()).photoUrl(m.getPhotoUrl())
            .dateAdhesion(m.getDateAdhesion()).notes(m.getNotes())
            .createdAt(m.getCreatedAt())
            .build();
    }

    private String genererNumero() {
        return "MR-" + LocalDateTime.now().getYear() + "-" +
               String.format("%05d", COUNTER.incrementAndGet());
    }
}
