package com.example.bookapi.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BookUploadRequestDTO {
    private String title;
    private String author;
    private String description;
    private MultipartFile file;
}