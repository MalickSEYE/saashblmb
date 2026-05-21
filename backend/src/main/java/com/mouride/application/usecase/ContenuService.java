package com.mouride.application.usecase;

import com.mouride.domain.model.ContenuReligieux;
import com.mouride.domain.repository.ContenuReligieuxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ContenuService {

    private final ContenuReligieuxRepository contenuRepository;

    public ContenuReligieux creer(ContenuReligieux contenu) {
        contenu.setNbVues(0);
        return contenuRepository.save(contenu);
    }

    public ContenuReligieux publier(ContenuReligieux contenu, UUID auteurId) {
        contenu.setPublieParId(auteurId);
        contenu.setEstPublie(true);
        return contenuRepository.save(contenu);
    }

    @Transactional(readOnly = true)
    public Page<ContenuReligieux> lister(ContenuReligieux.Type type, boolean publieOnly, Pageable pageable) {
        if (type != null && publieOnly) return contenuRepository.findByTypeAndEstPublieTrue(type, pageable);
        if (type != null)               return contenuRepository.findByType(type, pageable);
        if (publieOnly)                 return contenuRepository.findByEstPublieTrue(pageable);
        return contenuRepository.findAll(pageable);
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
