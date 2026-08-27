package com.migopan.api.dto;

import com.migopan.api.model.Usuario;
import java.math.BigDecimal;

public record UsuarioResponseDTO( 
    Long id, 
    String nome, 
    String email,
    Boolean emailVerificado,
    Boolean ativo,
    Integer streak,
    BigDecimal dinheiro 
) {
    public UsuarioResponseDTO(Usuario usuario) {
        this(
            usuario.getId(), 
            usuario.getNome(), 
            usuario.getEmail(), 
            usuario.getEmailVerificado(), 
            usuario.getAtivo(), 
            usuario.getStreak(), 
            usuario.getDinheiro()
        );
    }
}