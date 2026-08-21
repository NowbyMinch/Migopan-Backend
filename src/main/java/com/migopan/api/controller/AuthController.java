package com.migopan.api.controller;

import com.migopan.api.model.Usuario;
import com.migopan.api.repository.UsuarioRepository;
import com.migopan.api.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        // if (usuario == null || passwordEncoder.matches(senha, usuario.getSenhaHash())) {
        //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Credenciais inválidas"));
        // }
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Usuário inexistente"));
        }
        else if (!passwordEncoder.matches(senha, usuario.getSenhaHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Senha incorreta"));
        }

        String token = jwtService.gerarToken(usuario.getEmail());

        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Defina com true se usar HTTPS em produção 
        cookie.setPath("/");
        cookie.setMaxAge(86400); // 1 dia 
        response.addCookie(cookie);

        return ResponseEntity.ok(Map.of("message", "Login realizado com sucesso"));
    }

    @PostMapping("/logout") 
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok(Map.of("message", "Logout realizado com sucesso"));
    }
}