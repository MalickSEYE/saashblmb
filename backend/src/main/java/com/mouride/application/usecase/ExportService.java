package com.mouride.application.usecase;

import com.mouride.domain.model.Membre;
import com.mouride.domain.repository.MembreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportService {

    private final MembreRepository membreRepository;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] exportMembresExcel() throws Exception {
        // Export simplifié CSV (sans Apache POI pour alléger le build)
        List<Membre> membres = membreRepository.findAll();
        StringBuilder csv = new StringBuilder();
        csv.append("N° Membre,Prénom,Nom,Email,Téléphone,Ville,Pays,Statut,Date adhésion\n");
        for (Membre m : membres) {
            csv.append(String.join(",",
                safe(m.getNumeroMembre()),
                safe(m.getPrenom()),
                safe(m.getNom()),
                safe(m.getEmail()),
                safe(m.getTelephone()),
                safe(m.getVille()),
                safe(m.getPays()),
                m.getStatut() != null ? m.getStatut().name() : "",
                m.getDateAdhesion() != null ? m.getDateAdhesion().format(FMT) : ""
            )).append("\n");
        }
        return csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    public byte[] genererCarteMembre(Membre m) throws Exception {
        // Carte membre en texte simple (sans iText pour alléger le build)
        String carte = "=== CARTE DE MEMBRE MOURIDE SAAS ===\n" +
            "N° Membre  : " + safe(m.getNumeroMembre()) + "\n" +
            "Nom        : " + safe(m.getPrenom()) + " " + safe(m.getNom()) + "\n" +
            "Statut     : " + (m.getStatut() != null ? m.getStatut().name() : "—") + "\n" +
            "Adhésion   : " + (m.getDateAdhesion() != null ? m.getDateAdhesion().format(FMT) : "—") + "\n" +
            "=====================================\n";
        return carte.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    public byte[] genererRapportFinancier(String periode, Object stats) throws Exception {
        String rapport = "=== RAPPORT FINANCIER MOURIDE SAAS ===\n" +
            "Période : " + periode + "\n" +
            "Généré le : " + java.time.LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n" +
            "======================================\n";
        return rapport.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private String safe(String s) { return s != null ? s.replace(",", " ") : ""; }
}
