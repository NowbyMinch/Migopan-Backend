package com.migopan.api.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.migopan.api.dto.LoginRequestDTO;
import com.migopan.api.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<?> me(@CookieValue(name = "token", required = false) String token) {
        return authService.usuarioDoToken(token)
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(Map.of(
                        "authenticated", true,
                        "id", u.getId(),
                        "email", u.getEmail(),
                        "nome", u.getNome())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("authenticated", false, "message", "Não autenticado")));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid LoginRequestDTO dto) {
        ResponseCookie cookie = authService.login(dto.email(), dto.senha());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "Login realizado com sucesso"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authService.logout().toString())
                .body(Map.of("message", "Logout realizado com sucesso"));
    }
}
