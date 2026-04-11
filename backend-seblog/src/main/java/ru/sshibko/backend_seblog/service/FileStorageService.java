package ru.sshibko.backend_seblog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize file storage", e);
        }
    }

    //TODO Custom Exception
    public String store(MultipartFile file) {
        try {
            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Created upload directory: {}", uploadDir);
            }
            String fileName = UUID.randomUUID().toString() + "."
                    + FilenameUtils.getExtension(file.getOriginalFilename());
            Path filePath = path.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Uploaded file: {}", filePath.toAbsolutePath());
            return fileName;
        } catch (IOException e) {
            log.error("Could not store file {}. Error: {}", file.getOriginalFilename(), e.getMessage());
            throw new RuntimeException("Could not store file " + file.getOriginalFilename(), e);
        }
    }
}
