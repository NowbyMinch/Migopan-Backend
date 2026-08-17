package com.migopan.api.dto;

import com.migopan.api.model.Usuario;

public record UsuarioResponseDTO( 
    Long id, 
    String nome, 
    String email,
    Boolean emailVerificado,
    Boolean ativo,
    Integer streak,
    Double dinheiro 
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
        )
    }
}