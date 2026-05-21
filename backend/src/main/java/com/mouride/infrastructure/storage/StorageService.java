package com.mouride.infrastructure.storage;

import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class StorageService {

    @Value("${mouride.minio.endpoint}")
    private String endpoint;

    @Value("${mouride.minio.access-key}")
    private String accessKey;

    @Value("${mouride.minio.secret-key}")
    private String secretKey;

    @Value("${mouride.minio.bucket-name}")
    private String bucketName;

    private MinioClient minioClient;

    @PostConstruct
    public void init() {
        minioClient = MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build();
        creerBucketSiAbsent();
    }

    private void creerBucketSiAbsent() {
        try {
            boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Bucket MinIO créé : {}", bucketName);
            }
        } catch (Exception e) {
            log.warn("MinIO non disponible : {}", e.getMessage());
        }
    }

    public String uploadFichier(MultipartFile file, String dossier) throws Exception {
        String extension = getExtension(file.getOriginalFilename());
        String objectName = dossier + "/" + UUID.randomUUID() + extension;
        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(is, file.getSize(), -1)
                .contentType(file.getContentType())
                .build());
        }
        return objectName;
    }

    public String getUrlTemporaire(String objectName) throws Exception {
        return minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .method(Method.GET)
                .expiry(1, TimeUnit.HOURS)
                .build());
    }

    public void supprimer(String objectName) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder()
            .bucket(bucketName).object(objectName).build());
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }
}
