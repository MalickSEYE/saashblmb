package com.mouride.application.usecase;

import com.mouride.application.dto.DahiraDtos.*;
import com.mouride.domain.model.Dahira;
import com.mouride.domain.repository.CotisationRepository;
import com.mouride.domain.repository.DahiraRepository;
import com.mouride.domain.repository.MembreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DahiraService {

    private final DahiraRepository dahiraRepository;
    private final MembreRepository membreRepository;
    private final CotisationRepository cotisationRepository;

    public Response creer(Request req) {
        Dahira d = Dahira.builder()
            .nom(req.getNom()).code(genererCode(req))
            .description(req.getDescription())
            .ville(req.getVille())
            .pays(req.getPays() != null ? req.getPays() : "Sénégal")
            .adresse(req.getAdresse())
            .telephone(req.getTelephone()).email(req.getEmail())
            .responsableId(req.getResponsableId())
            .dateCreation(req.getDateCreation()).active(true)
            .build();
        return toResponse(dahiraRepository.save(d));
    }

    public Response modifier(UUID id, Request req) {
        Dahira d = dahiraRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Dahira introuvable : " + id));
        d.setNom(req.getNom()); d.setDescription(req.getDescription());
        d.setVille(req.getVille()); d.setPays(req.getPays());
        d.setAdresse(req.getAdresse()); d.setTelephone(req.getTelephone());
        d.setEmail(req.getEmail()); d.setResponsableId(req.getResponsableId());
        return toResponse(dahiraRepository.save(d));
    }

    @Transactional(readOnly = true)
    public List<Response> listerActifs() {
        return dahiraRepository.findByActiveTrue().stream()
            .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Response findById(UUID id) {
        return dahiraRepository.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new RuntimeException("Dahira introuvable : " + id));
    }

    public void desactiver(UUID id) {
        dahiraRepository.findById(id).ifPresent(d -> {
            d.setActive(false);
            dahiraRepository.save(d);
        });
    }

    private Response toResponse(Dahira d) {
        long nbMembres = membreRepository.countByDahiraId(d.getId());
        BigDecimal total = cotisationRepository.sumByDahira(d.getId());
        return Response.builder()
            .id(d.getId()).nom(d.getNom()).code(d.getCode())
            .description(d.getDescription()).ville(d.getVille()).pays(d.getPays())
            .adresse(d.getAdresse()).telephone(d.getTelephone()).email(d.getEmail())
            .responsableId(d.getResponsableId()).dateCreation(d.getDateCreation())
            .active(d.isActive()).nombreMembres(nbMembres)
            .totalCotisations(total != null ? total : BigDecimal.ZERO)
            .build();
    }

    private String genererCode(Request req) {
        if (req.getCode() != null && !req.getCode().isBlank()) return req.getCode();
        String pays = req.getPays() != null ? req.getPays().substring(0, 2).toUpperCase() : "SN";
        String nom  = req.getNom().replaceAll("[^A-Za-z]", "").substring(0, Math.min(3, req.getNom().length())).toUpperCase();
        return pays + "-" + nom + "-" + String.format("%02d", (int)(Math.random() * 99 + 1));
    }
}
