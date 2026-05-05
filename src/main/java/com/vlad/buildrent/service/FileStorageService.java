package com.vlad.buildrent.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    @Value("${buildrent.uploads-dir:./uploads}")
    private String uploadsDir;

    private Path root;

    @PostConstruct
    public void init() {
        root = Paths.get(uploadsDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(root);
            log.info("Uploads directory: {}", root);
        } catch (IOException e) {
            throw new IllegalStateException("Не вдалося створити каталог uploads: " + root, e);
        }
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл порожній");
        }
        String originalName = file.getOriginalFilename() == null ? "image" : file.getOriginalFilename();
        String ext = extension(originalName).toLowerCase(Locale.ROOT);
        if (!ext.matches("\\.(jpg|jpeg|png|webp|gif)")) {
            throw new IllegalArgumentException("Дозволено лише зображення (jpg, png, webp, gif)");
        }
        String name = UUID.randomUUID() + ext;
        Path target = root.resolve(name);
        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Помилка збереження файлу", e);
        }
        return "/uploads/" + name;
    }

    public void delete(String url) {
        if (url == null || !url.startsWith("/uploads/")) return;
        String name = url.substring("/uploads/".length());
        try {
            Files.deleteIfExists(root.resolve(name));
        } catch (IOException e) {
            log.warn("Не вдалося видалити файл {}: {}", name, e.getMessage());
        }
    }

    private String extension(String name) {
        int idx = name.lastIndexOf('.');
        return idx >= 0 ? name.substring(idx) : "";
    }
}
