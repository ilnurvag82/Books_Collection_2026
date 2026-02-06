package com.example.bookapi.controller;

import com.example.bookapi.dto.AuthResponseDTO;
import com.example.bookapi.dto.LoginRequestDTO;
import com.example.bookapi.dto.RegisterRequestDTO;
import com.example.bookapi.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API для регистрации и аутентификации")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final CaptchaService captchaService;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request) {
        // Проверка капчи
        //if (!captchaService.verifyCaptcha(request.getCaptchaResponse())) {
           // return ResponseEntity.badRequest().body("Invalid captcha");
        //}

        userService.registerUser(request);
        return ResponseEntity.ok("Registration successful. Please check your email for verification.");
    }

    @GetMapping("/verify")
    @Operation(summary = "Подтверждение email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        boolean verified = userService.verifyUser(token);
        if (verified) {
            return ResponseEntity.ok("Email verified successfully. You can now login.");
        } else {
            return ResponseEntity.badRequest().body("Verification failed");
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Вход в систему")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        org.springframework.security.core.userdetails.UserDetails userDetails =
                customUserDetailsService.loadUserByUsername(request.getUsernameOrEmail());

        String jwt = jwtService.generateToken(userDetails);

        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(jwt);
        response.setUsername(userDetails.getUsername());

        return ResponseEntity.ok(response);
    }
}