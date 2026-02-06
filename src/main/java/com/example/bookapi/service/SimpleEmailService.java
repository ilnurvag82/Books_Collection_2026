package com.example.bookapi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SimpleEmailService {

    // Простое хранилище для токенов (в памяти)
    private final Map<String, String> tokenStorage = new HashMap<>();

    /**
     * Простая заглушка для отправки email
     * Вместо реальной отправки просто выводит в консоль
     */
    public void sendVerificationEmail(String toEmail, String token) {
        try {
            // Сохраняем токен для тестирования
            tokenStorage.put(toEmail, token);

            String verificationUrl = "http://localhost:8080/api/auth/verify?token=" + token;

            // Красиво выводим в консоль
            System.out.println("\n" + "=".repeat(60));
            System.out.println("📧 SIMPLE EMAIL SERVICE");
            System.out.println("=".repeat(60));
            System.out.println("📩 Кому: " + toEmail);
            System.out.println("🔗 Ссылка для подтверждения:");
            System.out.println(verificationUrl);
            System.out.println("🔑 Токен: " + token);
            System.out.println("=".repeat(60));
            System.out.println("\n🚀 Для тестирования скопируйте эту ссылку:");
            System.out.println("http://localhost:8080/api/auth/verify?token=" + token);
            System.out.println();

            log.info("Email 'отправлен' на: {} с токеном: {}", toEmail, token);

        } catch (Exception e) {
            log.error("Ошибка в SimpleEmailService: {}", e.getMessage());
        }
    }

    /**
     * Проверка токена
     */
    public boolean verifyToken(String email, String token) {
        String savedToken = tokenStorage.get(email);
        boolean isValid = savedToken != null && savedToken.equals(token);

        log.info("Проверка токена для {}: {}", email,
                isValid ? "ВЕРНЫЙ" : "НЕВЕРНЫЙ");

        return isValid;
    }

    /**
     * Получить токен по email (для тестов)
     */
    public String getToken(String email) {
        return tokenStorage.get(email);
    }

    /**
     * Очистить хранилище
     */
    public void clearTokens() {
        tokenStorage.clear();
        log.info("Хранилище токенов очищено");
    }
}