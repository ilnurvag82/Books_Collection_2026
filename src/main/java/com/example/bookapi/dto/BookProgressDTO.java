package com.example.bookapi.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookProgressDTO {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private Integer currentPage;
    private Integer totalPages;
    private Double percentage;
    private LocalDateTime lastReadAt;
}