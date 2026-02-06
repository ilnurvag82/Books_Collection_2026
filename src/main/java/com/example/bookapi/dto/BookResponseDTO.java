package com.example.bookapi.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String author;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private Integer totalPages;
    private String conversionStatus;  // ← НОВОЕ ПОЛЕ
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}