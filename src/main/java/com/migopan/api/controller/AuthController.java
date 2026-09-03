package com.migopan.api.controller;

import com.migopan.api.model.Usuario;
import com.migopan.api.repository.UsuarioRepository;
import com.migopan.api.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpServletResponse response){
        String email = credentials.get("email");
        String senha = credentials.get("senha");

        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null || !passwordEncoder.matches(senha, usuario.getSenhaHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Credenciais inválidas"));
        }   

        String token = jwtService.gerarToken(usuario.getEmail());

        // Uso profissional do ResponseCookie compatível com ambientes desacoplados
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(false) // Mude para true apenas se sua aplicação estiver em HTTPS em produção
                .path("/")
                .maxAge(86400) // 1 dia
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(Map.of("message", "Login realizado com sucesso"));
    }

    @PostMapping("/logout") 
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0) // Expira e remove o cookie do navegador imediatamente
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(Map.of("message", "Logout realizado com sucesso"));
    }
}