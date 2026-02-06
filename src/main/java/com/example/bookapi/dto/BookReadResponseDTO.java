package com.example.bookapi.dto;

import lombok.Data;

@Data
public class BookReadResponseDTO {
    private BookResponseDTO book;
    private String htmlContent;
    private BookProgressDTO progress;
    private Integer currentPage;
}