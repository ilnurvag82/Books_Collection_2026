package com.example.bookapi.service;

import com.example.bookapi.entity.Book;
import com.example.bookapi.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookConversionService {

    private final BookRepository bookRepository;
    private final FileStorageService fileStorageService;

    @Async
    @Transactional
    public void convertBookToHtmlAsync(Long bookId) {
        try {
            log.info("Начинаем конвертацию книги ID: {}", bookId);

            // Получаем книгу через репозиторий
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));

            // Обновляем статус
            book.setConversionStatus(Book.ConversionStatus.PROCESSING);
            bookRepository.save(book);

            // Имитируем конвертацию (в реальном проекте используйте библиотеку типа Apache PDFBox)
            Thread.sleep(5000); // Задержка для имитации работы

            // Создаем простой HTML
            String htmlContent = createHtmlFromBook(book);
            String htmlFileName = generateHtmlFileName(book.getFileName());

            // Сохраняем HTML
            fileStorageService.saveHtmlContent(htmlFileName, htmlContent);

            // Обновляем книгу
            book.setHtmlPath(htmlFileName);
            book.setTotalPages(100); // Примерное количество страниц
            book.setConversionStatus(Book.ConversionStatus.COMPLETED);
            bookRepository.save(book);

            log.info("Конвертация книги ID: {} завершена", bookId);

        } catch (Exception e) {
            log.error("Ошибка при конвертации книги ID: {}", bookId, e);
            updateBookStatusOnFailure(bookId);
        }
    }

    private String createHtmlFromBook(Book book) {
        // Простая имитация создания HTML
        // В реальном проекте используйте библиотеку для конвертации PDF/DOCX в HTML
        String description = book.getDescription() != null ? book.getDescription() : "";

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>%s</title>
                <style>
                    body { 
                        font-family: Arial, sans-serif; 
                        margin: 40px; 
                        line-height: 1.6;
                    }
                    .page { 
                        page-break-after: always; 
                        padding: 20px;
                        max-width: 800px;
                        margin: 0 auto;
                    }
                    h1 { 
                        color: #333; 
                        border-bottom: 2px solid #4CAF50;
                        padding-bottom: 10px;
                    }
                    h2 {
                        color: #555;
                        margin-top: 30px;
                    }
                    p {
                        margin: 15px 0;
                    }
                    .book-info {
                        background-color: #f9f9f9;
                        padding: 15px;
                        border-radius: 5px;
                        margin-bottom: 20px;
                    }
                </style>
            </head>
            <body>
                <div class="page">
                    <div class="book-info">
                        <h1>%s</h1>
                        <h2>Автор: %s</h2>
                    </div>
                    <div class="content">
                        <h2>Описание:</h2>
                        <p>%s</p>
                    </div>
                    <div style="margin-top: 40px; text-align: center; color: #777;">
                        <p><em>Конвертировано в HTML для удобного чтения</em></p>
                    </div>
                </div>
            </body>
            </html>
            """,
                book.getTitle(),
                book.getTitle(),
                book.getAuthor(),
                description
        );
    }

    private String generateHtmlFileName(String originalFileName) {
        if (originalFileName == null) {
            return "book_" + System.currentTimeMillis() + ".html";
        }

        // Удаляем расширение и добавляем .html
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return originalFileName.substring(0, dotIndex) + ".html";
        }
        return originalFileName + ".html";
    }

    private void updateBookStatusOnFailure(Long bookId) {
        try {
            Book book = bookRepository.findById(bookId).orElse(null);
            if (book != null) {
                book.setConversionStatus(Book.ConversionStatus.FAILED);
                bookRepository.save(book);
                log.info("Статус книги ID: {} обновлен на FAILED", bookId);
            }
        } catch (Exception ex) {
            log.error("Не удалось обновить статус книги ID: {}", bookId, ex);
        }
    }
}