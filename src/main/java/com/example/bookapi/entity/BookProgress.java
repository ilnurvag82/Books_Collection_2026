package com.example.bookapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "book_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "current_page")
    private Integer currentPage = 0;

    @Column(name = "total_pages")
    private Integer totalPages;

    @Column(name = "percentage")
    private Double percentage = 0.0;

    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;

    @PreUpdate
    @PrePersist
    protected void onUpdate() {
        lastReadAt = LocalDateTime.now();
        if (totalPages != null && totalPages > 0) {
            percentage = (currentPage * 100.0) / totalPages;
        }
    }
}