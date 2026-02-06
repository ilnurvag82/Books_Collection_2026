package com.example.bookapi.service;

import com.example.bookapi.dto.*;
import com.example.bookapi.entity.Book;
import com.example.bookapi.entity.BookProgress;
import com.example.bookapi.entity.User;
import com.example.bookapi.repository.BookProgressRepository;
import com.example.bookapi.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookProgressRepository bookProgressRepository;
    private final FileStorageService fileStorageService;
    private final BookConversionService bookConversionService;
    private final UserService userService;

    @Transactional
    public BookResponseDTO uploadBook(BookUploadRequestDTO request) throws IOException {
        // Сохраняем файл
        String fileName = fileStorageService.storeFile(request.getFile());

        // Создаем книгу
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setDescription(request.getDescription());
        book.setFileName(fileName);
        book.setFilePath(fileStorageService.getFilePath(fileName).toString());
        book.setFileSize(request.getFile().getSize());
        book.setFileType(request.getFile().getContentType());
        book.setConversionStatus(Book.ConversionStatus.PENDING);

        Book savedBook = bookRepository.save(book);

        // Запускаем асинхронную конвертацию
        bookConversionService.convertBookToHtmlAsync(savedBook.getId());

        return convertToDTO(savedBook);
    }

    @Transactional
    public BookReadResponseDTO readBook(Long bookId, Integer page) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));

        User currentUser = getCurrentUser();

        // Получаем или создаем прогресс
        BookProgress progress = bookProgressRepository.findByUserAndBook(currentUser, book)
                .orElseGet(() -> {
                    BookProgress newProgress = new BookProgress();
                    newProgress.setUser(currentUser);
                    newProgress.setBook(book);
                    newProgress.setTotalPages(book.getTotalPages());
                    return newProgress;
                });

        // Обновляем текущую страницу
        if (page != null && page >= 0) {
            progress.setCurrentPage(page);
        }

        // Сохраняем прогресс
        bookProgressRepository.save(progress);

        // Читаем HTML контент
        String htmlContent = null;
        if (book.getHtmlPath() != null && book.getConversionStatus() == Book.ConversionStatus.COMPLETED) {
            try {
                htmlContent = fileStorageService.readHtmlContent(book.getHtmlPath());
            } catch (IOException e) {
                throw new RuntimeException("Не удалось прочитать HTML книгу", e);
            }
        }

        // Формируем ответ
        BookReadResponseDTO response = new BookReadResponseDTO();
        response.setBook(convertToDTO(book));
        response.setHtmlContent(htmlContent);
        response.setProgress(convertToProgressDTO(progress));
        response.setCurrentPage(progress.getCurrentPage());

        return response;
    }

    @Transactional
    public BookProgressDTO updateProgress(Long bookId, Integer page) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));

        User currentUser = getCurrentUser();

        BookProgress progress = bookProgressRepository.findByUserAndBook(currentUser, book)
                .orElseThrow(() -> new RuntimeException("Прогресс не найден"));

        progress.setCurrentPage(page);
        if (book.getTotalPages() != null) {
            progress.setTotalPages(book.getTotalPages());
        }

        bookProgressRepository.save(progress);
        return convertToProgressDTO(progress);
    }

    public List<BookProgressDTO> getUserProgress() {
        User currentUser = getCurrentUser();
        return bookProgressRepository.findAll().stream()
                .filter(progress -> progress.getUser().getId().equals(currentUser.getId()))
                .map(this::convertToProgressDTO)
                .collect(Collectors.toList());
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.getUserByUsername(username);
    }

    @Transactional
    public BookResponseDTO createBook(BookRequestDTO bookRequest) {
        Book book = new Book();
        book.setTitle(bookRequest.getTitle());
        book.setAuthor(bookRequest.getAuthor());
        book.setDescription(bookRequest.getDescription());
        book.setConversionStatus(Book.ConversionStatus.PENDING);

        Book savedBook = bookRepository.save(book);
        return convertToDTO(savedBook);
    }

    public List<BookResponseDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BookResponseDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        return convertToDTO(book);
    }

    public BookResponseDTO updateBook(Long id, BookRequestDTO bookRequest) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        book.setTitle(bookRequest.getTitle());
        book.setDescription(bookRequest.getDescription());
        book.setAuthor(bookRequest.getAuthor());

        Book updatedBook = bookRepository.save(book);
        return convertToDTO(updatedBook);
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    public List<BookResponseDTO> searchByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookResponseDTO> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Вспомогательные методы
    private BookResponseDTO convertToDTO(Book book) {
        BookResponseDTO dto = new BookResponseDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setDescription(book.getDescription());
        dto.setAuthor(book.getAuthor());
        dto.setFileName(book.getFileName());
        dto.setFileSize(book.getFileSize());
        dto.setFileType(book.getFileType());
        dto.setTotalPages(book.getTotalPages());
        dto.setConversionStatus(book.getConversionStatus() != null ? book.getConversionStatus().name() : null);
        dto.setCreatedAt(book.getCreatedAt());
        dto.setUpdatedAt(book.getUpdatedAt());
        return dto;
    }

    private BookProgressDTO convertToProgressDTO(BookProgress progress) {
        BookProgressDTO dto = new BookProgressDTO();
        dto.setId(progress.getId());
        dto.setBookId(progress.getBook().getId());
        dto.setBookTitle(progress.getBook().getTitle());
        dto.setCurrentPage(progress.getCurrentPage());
        dto.setTotalPages(progress.getTotalPages());
        dto.setPercentage(progress.getPercentage());
        dto.setLastReadAt(progress.getLastReadAt());
        return dto;
    }

    // Этот метод оставлен для совместимости (если используется в других местах)
    public Book getBookEntityById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }

    // Этот метод оставлен для совместимости (если используется в других местах)
    public void saveBook(Book book) {
        bookRepository.save(book);
    }
}