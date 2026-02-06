package com.example.bookapi.dto;

import lombok.Data;

@Data
public class BookRequestDTO {
    private String title;
    private String description;
    private String author;
}