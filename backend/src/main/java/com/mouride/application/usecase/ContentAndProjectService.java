package com.mouride.application.usecase;

import com.mouride.domain.model.ContenuReligieux;
import com.mouride.domain.model.ProjetSocial;
import com.mouride.domain.repository.ContenuReligieuxRepository;
import com.mouride.domain.repository.ProjetSocialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

// ── ContenuService ────────────────────────────────────────
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
class ContenuService {

    private final ContenuReligieuxRepository contenuRepository;

    public ContenuReligieux publier(ContenuReligieux contenu, UUID auteurId) {
        contenu.setPublieParId(auteurId);
        contenu.setEstPublie(true);
        return contenuRepository.save(contenu);
    }

    public ContenuReligieux creer(ContenuReligieux contenu) {
        contenu.setNbVues(0);
        return contenuRepository.save(contenu);
    }

    @Transactional(readOnly = true)
    public Page<ContenuReligieux> lister(ContenuReligieux.Type type, boolean publieOnly, Pageable pageable) {
        if (type != null && publieOnly) return contenuRepository.findByTypeAndEstPublieTrue(type, pageable);
        if (type != null)               return contenuRepository.findByType(type, pageable);
        if (publieOnly)                 return contenuRepository.findByEstPublieTrue(pageable);
        return contenuRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<ContenuReligieux> rechercher(String query, Pageable pageable) {
        return contenuRepository.search(query, pageable);
    }

    public void incrementerVues(UUID id) {
        contenuRepository.findById(id).ifPresent(c -> {
            c.setNbVues(c.getNbVues() + 1);
            contenuRepository.save(c);
        });
    }

    public void supprimer(UUID id) {
        contenuRepository.deleteById(id);
    }
}

// ── ProjetService ─────────────────────────────────────────
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
class ProjetService {

    private final ProjetSocialRepository projetRepository;

    public ProjetSocial creer(ProjetSocial projet) {
        projet.setMontantCollecte(BigDecimal.ZERO);
        projet.setStatut(ProjetSocial.Statut.PLANIFIE);
        return projetRepository.save(projet);
    }

    public ProjetSocial modifier(UUID id, ProjetSocial updated) {
        ProjetSocial p = projetRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Projet introuvable"));
        p.setTitre(updated.getTitre());
        p.setDescription(updated.getDescription());
        p.setBudgetCible(updated.getBudgetCible());
        p.setDateDebut(updated.getDateDebut());
        p.setDateFin(updated.getDateFin());
        p.setStatut(updated.getStatut());
        return projetRepository.save(p);
    }

    public void ajouterDon(UUID projetId, BigDecimal montant) {
        projetRepository.findById(projetId).ifPresent(p -> {
            p.setMontantCollecte(p.getMontantCollecte().add(montant));
            projetRepository.save(p);
        });
    }

    @Transactional(readOnly = true)
    public Page<ProjetSocial> lister(Pageable pageable) {
        return projetRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Transactional(readOnly = true)
    public ProjetSocial findById(UUID id) {
        return projetRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Projet introuvable"));
    }
}
