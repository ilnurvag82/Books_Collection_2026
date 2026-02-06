package com.example.bookapi.service;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.html.dir:html-books}")
    private String htmlDir;

    public String storeFile(MultipartFile file) throws IOException {
        // Создаем уникальное имя файла
        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);
        String fileName = UUID.randomUUID().toString() + fileExtension;

        // Создаем директорию если её нет
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        // Сохраняем файл
        Path targetLocation = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        log.info("Файл сохранен: {}", targetLocation.toString());
        return fileName;
    }

    public Path getFilePath(String fileName) {
        return Paths.get(uploadDir).resolve(fileName).toAbsolutePath().normalize();
    }

    public Path getHtmlFilePath(String fileName) {
        return Paths.get(htmlDir).resolve(fileName).toAbsolutePath().normalize();
    }

    public void saveHtmlContent(String htmlFileName, String content) throws IOException {
        Path htmlPath = Paths.get(htmlDir).toAbsolutePath().normalize();
        Files.createDirectories(htmlPath);

        Path targetLocation = htmlPath.resolve(htmlFileName);
        Files.writeString(targetLocation, content);
    }

    public String readHtmlContent(String htmlFileName) throws IOException {
        Path htmlPath = getHtmlFilePath(htmlFileName);
        if (Files.exists(htmlPath)) {
            return Files.readString(htmlPath);
        }
        return null;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int lastDotIndex = fileName.lastIndexOf(".");
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
    }
}