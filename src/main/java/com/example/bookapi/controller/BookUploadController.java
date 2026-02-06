package com.example.bookapi.controller;

import com.example.bookapi.dto.BookProgressDTO;
import com.example.bookapi.dto.BookReadResponseDTO;
import com.example.bookapi.dto.BookUploadRequestDTO;
import com.example.bookapi.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Book Upload & Reading", description = "API для загрузки и чтения книг")
@SecurityRequirement(name = "bearerAuth")
public class BookUploadController {

    private final BookService bookService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Загрузить книгу (файл)")
    public ResponseEntity<?> uploadBook(
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("file") MultipartFile file) {

        try {
            BookUploadRequestDTO request = new BookUploadRequestDTO();
            request.setTitle(title);
            request.setAuthor(author);
            request.setDescription(description);
            request.setFile(file);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(bookService.uploadBook(request));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при загрузке файла: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/read")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Читать книгу с сохранением страницы")
    public ResponseEntity<BookReadResponseDTO> readBook(
            @PathVariable Long id,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page) {

        BookReadResponseDTO response = bookService.readBook(id, page);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/progress")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Обновить прогресс чтения")
    public ResponseEntity<BookProgressDTO> updateProgress(
            @PathVariable Long id,
            @RequestParam("page") Integer page) {

        BookProgressDTO progress = bookService.updateProgress(id, page);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/progress")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Получить прогресс чтения всех книг")
    public ResponseEntity<List<BookProgressDTO>> getUserProgress() {
        List<BookProgressDTO> progressList = bookService.getUserProgress();
        return ResponseEntity.ok(progressList);
    }
}