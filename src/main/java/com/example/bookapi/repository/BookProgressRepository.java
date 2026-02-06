package com.example.bookapi.repository;

import com.example.bookapi.entity.BookProgress;
import com.example.bookapi.entity.User;
import com.example.bookapi.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookProgressRepository extends JpaRepository<BookProgress, Long> {
    Optional<BookProgress> findByUserAndBook(User user, Book book);
    Optional<BookProgress> findByUserIdAndBookId(Long userId, Long bookId);
}