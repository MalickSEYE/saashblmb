package com.mouride.application.usecase;

import com.mouride.domain.model.ProjetSocial;
import com.mouride.domain.repository.ProjetSocialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjetService {

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

    @Transactional(readOnly = true)
    public Page<ProjetSocial> lister(Pageable pageable) {
        return projetRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public ProjetSocial findById(UUID id) {
        return projetRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Projet introuvable"));
    }
}
