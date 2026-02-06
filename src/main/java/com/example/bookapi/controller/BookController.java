package com.example.bookapi.controller;

import com.example.bookapi.dto.BookRequestDTO;
import com.example.bookapi.dto.BookResponseDTO;
import com.example.bookapi.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Book Controller", description = "API для управления книгами")
@SecurityRequirement(name = "bearerAuth")
public class BookController {

    private final BookService bookService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Создать новую книгу")
    public ResponseEntity<BookResponseDTO> createBook(@RequestBody BookRequestDTO bookRequest) {
        BookResponseDTO createdBook = bookService.createBook(bookRequest);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Получить все книги")
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        List<BookResponseDTO> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить книгу по ID")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable Long id) {
        BookResponseDTO book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Обновить книгу")
    public ResponseEntity<BookResponseDTO> updateBook(
            @PathVariable Long id,
            @RequestBody BookRequestDTO bookRequest) {
        BookResponseDTO updatedBook = bookService.updateBook(id, bookRequest);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Удалить книгу")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/author")
    @Operation(summary = "Поиск книг по автору")
    public ResponseEntity<List<BookResponseDTO>> searchByAuthor(@RequestParam String author) {
        List<BookResponseDTO> books = bookService.searchByAuthor(author);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/search/title")
    @Operation(summary = "Поиск книг по названию")
    public ResponseEntity<List<BookResponseDTO>> searchByTitle(@RequestParam String title) {
        List<BookResponseDTO> books = bookService.searchByTitle(title);
        return ResponseEntity.ok(books);
    }
}