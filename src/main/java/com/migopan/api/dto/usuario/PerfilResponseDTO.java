package com.migopan.api.dto;

import com.migopan.api.model.Usuario;
import java.math.BigDecimal;

public record PerfilResponseDTO( 
    Long id, 
    String nome, 
    Boolean ativo
) {
    public PerfilResponseDTO(Usuario usuario) {
        this(
            usuario.getId(), 
            usuario.getNome(), 
            usuario.getAtivo() 
        );
    }
}