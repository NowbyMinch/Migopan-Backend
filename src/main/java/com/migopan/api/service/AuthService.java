package com.migopan.api.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.migopan.api.exception.CredenciaisInvalidasException;
import com.migopan.api.model.Usuario;
import com.migopan.api.repository.UsuarioRepository;
import com.migopan.api.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private static final String COOKIE = "token";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    public ResponseCookie login(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!Boolean.TRUE.equals(usuario.getAtivo())
                || !passwordEncoder.matches(senha, usuario.getSenhaHash())) {
            throw new CredenciaisInvalidasException();
        }
        return cookie(jwtService.gerarToken(usuario.getEmail()), jwtService.getExpirationSeconds());
    }

    public ResponseCookie logout() {
        return cookie("", 0);
    }

    /** Usuário dono do token, se o token for válido e o usuário estiver ativo. */
    public Optional<Usuario> usuarioDoToken(String token) {
        if (token == null || token.isBlank() || !jwtService.validarToken(token)) {
            return Optional.empty();
        }
        return usuarioRepository.findByEmail(jwtService.extrairEmail(token))
                .filter(u -> Boolean.TRUE.equals(u.getAtivo()));
    }

    private ResponseCookie cookie(String valor, long maxAgeSegundos) {
        return ResponseCookie.from(COOKIE, valor)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(maxAgeSegundos)
                .sameSite("Lax")
                .build();
    }
}
