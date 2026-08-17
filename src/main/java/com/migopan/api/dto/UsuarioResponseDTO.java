package com.migopan.api.dto;

import com.migopan.api.model.Usuario;
<<<<<<< HEAD

public record UsuarioResponseDTO( Long id, String nome, String email, )
=======
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
>>>>>>> e9e3ff024191fb9e9f842e7fdd4065b06442f57d
