package com.migopan.api.dto;

import com.migopan.api.model.Usuario;
import java.math.BigDecimal;

public record UsuarioResponseDTO (
    Long id, 
    String nome,
    String email,
    Boolean emailVerificado,
    Integer Streak,
    BigDecimal dinheiro,
    Boolean ativo
){
    public UsuarioResponseDTO(Usuario usuario){
        this(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getEmailVerificado(),
            usuario.getStreak(),
            usuario.getDinheiro(),
            usuario.getAtivo()
        );
    }
}