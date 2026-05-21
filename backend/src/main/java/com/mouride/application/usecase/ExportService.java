package com.mouride.application.usecase;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.mouride.domain.model.Membre;
import com.mouride.domain.repository.MembreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportService {

    private final MembreRepository membreRepository;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── Export Excel ──────────────────────────────────────
    public byte[] exportMembresExcel() throws Exception {
        List<Membre> membres = membreRepository.findAll();
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Membres");

            // Style en-tête
            CellStyle headerStyle = wb.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font hFont = wb.createFont();
            hFont.setBold(true); hFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(hFont);

            // En-têtes
            String[] headers = {"N° Membre","Prénom","Nom","Email","Téléphone",
                                 "Ville","Pays","Statut","Date adhésion"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 5000);
            }

            // Données
            int rowNum = 1;
            for (Membre m : membres) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(m.getNumeroMembre());
                row.createCell(1).setCellValue(m.getPrenom());
                row.createCell(2).setCellValue(m.getNom());
                row.createCell(3).setCellValue(m.getEmail() != null ? m.getEmail() : "");
                row.createCell(4).setCellValue(m.getTelephone() != null ? m.getTelephone() : "");
                row.createCell(5).setCellValue(m.getVille() != null ? m.getVille() : "");
                row.createCell(6).setCellValue(m.getPays() != null ? m.getPays() : "");
                row.createCell(7).setCellValue(m.getStatut().name());
                row.createCell(8).setCellValue(m.getDateAdhesion() != null ?
                    m.getDateAdhesion().format(FMT) : "");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();
        }
    }

    // ── Carte membre PDF ──────────────────────────────────
    public byte[] genererCarteMembre(Membre m) throws Exception {
        Document doc = new Document(new Rectangle(243f, 153f)); // Taille carte bancaire
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);
        doc.open();

        // Fond vert
        PdfContentByte canvas = PdfWriter.getInstance(doc, out).getDirectContentUnder();
        canvas.setColorFill(new BaseColor(26, 71, 49));
        canvas.rectangle(0, 0, 243, 153);
        canvas.fill();

        // Logo / titre
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, new BaseColor(240, 201, 107));
        Font textFont  = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.WHITE);
        Font numFont   = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, new BaseColor(240, 201, 107));

        doc.add(new Paragraph("☪ MOURIDE SAAS", titleFont));
        doc.add(new Paragraph("Carte de membre officielle", textFont));
        doc.add(Chunk.NEWLINE);
        doc.add(new Paragraph(m.getPrenom().toUpperCase() + " " + m.getNom().toUpperCase(), textFont));
        doc.add(new Paragraph("N° " + m.getNumeroMembre(), numFont));
        doc.add(new Paragraph("Statut : " + m.getStatut().name(), textFont));
        if (m.getDahiraId() != null)
            doc.add(new Paragraph("Dahira membre", textFont));
        doc.add(new Paragraph("Adhésion : " +
            (m.getDateAdhesion() != null ? m.getDateAdhesion().format(FMT) : "—"), textFont));

        doc.close();
        return out.toByteArray();
    }

    // ── Rapport financier PDF ─────────────────────────────
    public byte[] genererRapportFinancier(String periode, Object stats) throws Exception {
        Document doc = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);
        doc.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, new BaseColor(26, 71, 49));
        Font subFont   = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.DARK_GRAY);
        Font bodyFont  = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);

        doc.add(new Paragraph("RAPPORT FINANCIER — " + periode.toUpperCase(), titleFont));
        doc.add(new Paragraph("Mouride SaaS Platform", bodyFont));
        doc.add(new Paragraph("Généré le : " + java.time.LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), bodyFont));
        doc.add(Chunk.NEWLINE);
        doc.add(new Paragraph("Ce rapport présente le bilan des cotisations et contributions", bodyFont));
        doc.add(new Paragraph("collectées sur la plateforme pour la période : " + periode, bodyFont));

        doc.close();
        return out.toByteArray();
    }
}
