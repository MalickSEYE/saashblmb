package com.mouride.infrastructure.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@Slf4j
public class StorageService {

    private static final Path UPLOAD_DIR = Paths.get("/tmp/mouride-uploads");

    public StorageService() {
        try {
            Files.createDirectories(UPLOAD_DIR);
        } catch (IOException e) {
            log.warn("Impossible de créer le dossier uploads : {}", e.getMessage());
        }
    }

    public String uploadFichier(MultipartFile file, String dossier) throws Exception {
        String extension = getExtension(file.getOriginalFilename());
        String fileName = dossier + "_" + UUID.randomUUID() + extension;
        Path dest = UPLOAD_DIR.resolve(fileName);
        Files.createDirectories(dest.getParent());
        file.transferTo(dest);
        log.info("Fichier uploadé : {}", dest);
        return fileName;
    }

    public String getUrlTemporaire(String objectName) {
        return "/api/v1/files/" + objectName;
    }

    public void supprimer(String objectName) {
        try {
            Files.deleteIfExists(UPLOAD_DIR.resolve(objectName));
        } catch (IOException e) {
            log.warn("Impossible de supprimer : {}", objectName);
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }
}
