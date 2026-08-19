package com.migopan.api.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    private final String SECRET_KEY = "asdsa";
    private final long EXPIRATION_TIME = 86400000;

    private SecretKey getSigninKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    };

    public String gerarToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigninKey())
                .compact();
    };

    public String extrairEmail(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(getSigninKey()) // Verifica se é do sistema
            .build() 
            .parseSignedClaims(token) // Pega pelo usuário logado
            .getPayload();
        return claims.getSubject();
    };

    public boolean validarToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigninKey()).build().parseSignedClaims(token);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    };

}
