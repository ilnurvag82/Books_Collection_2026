package com.example.bookapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String author;

    @Column(name = "file_name")
    private String fileName;  // ← НОВОЕ ПОЛЕ

    @Column(name = "file_path")
    private String filePath;  // ← НОВОЕ ПОЛЕ

    @Column(name = "file_size")
    private Long fileSize;    // ← НОВОЕ ПОЛЕ

    @Column(name = "file_type")
    private String fileType;  // ← НОВОЕ ПОЛЕ

    @Column(name = "html_path")
    private String htmlPath;  // ← НОВОЕ: путь к HTML версии

    @Column(name = "total_pages")
    private Integer totalPages;  // ← НОВОЕ: количество страниц

    @Column(name = "conversion_status")
    @Enumerated(EnumType.STRING)
    private ConversionStatus conversionStatus = ConversionStatus.PENDING;  // ← НОВОЕ

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ConversionStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}