package ru.sshibko.backend_seblog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
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
            Files.createDirectories(Paths.get(uploadDir, "avatars"));
            Files.createDirectories(Paths.get(uploadDir, "posts"));
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize file storage", e);
        }
    }

    public String storeAvatar(MultipartFile file) {
        return store(file, "avatars", false);
    }

    public String storePostImage(MultipartFile file) {
        return store(file, "posts", true);
    }

    public String store(MultipartFile file, String subFolder, boolean returnFullPath) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл не может быть пустым");
        }

        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new IllegalArgumentException("Разрешены только файлы изображений");
        }

        if (file.getSize() > 5 * 1024 * 1024) { // 5 МБ
            throw new IllegalArgumentException("Размер файла не должен превышать 5 МБ");
        }

        try {
            Path rootPath = Paths.get(uploadDir);
            Path targetDirectory = rootPath.resolve(subFolder);

            if (!Files.exists(targetDirectory)) {
                Files.createDirectories(targetDirectory);
                log.info("Created upload target directory: {}", targetDirectory);
            }

            String originalName = file.getOriginalFilename();
            String extension = originalName != null && originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf(".")).toLowerCase()
                    : ".jpg";

            if (!extension.matches("\\.(jpg|jpeg|png|gif|webp)$")) {
                throw new IllegalArgumentException("Неподдерживаемый формат файла. Разрешены: jpg, png, gif, webp");
            }

            String fileName = UUID.randomUUID().toString() + extension;
            Path filePath = targetDirectory.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Uploaded file to {}: {}", subFolder, filePath.toAbsolutePath());

            if (returnFullPath) {
                return "/api/v1/uploads/" + subFolder + "/" + fileName;
            }

            return fileName;

        } catch (IOException e) {
            log.error("Could not store file {}. Error: {}", file.getOriginalFilename(), e.getMessage());
            throw new RuntimeException("Could not store file " + file.getOriginalFilename(), e);
        }
    }
}
