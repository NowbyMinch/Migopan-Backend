package com.migopan.api.dto;

import com.migopan.api.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class UsuarioDTOs {

    public record AtualizarUsuarioRequestDTO(
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @Email(message = "Formato de email inválido")
        @Size(max = 255, message = "O email deve ter no máximo 255 caracteres")
        String email
    ) {}

    public record UsuarioRequestDTO(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Formato de email inválido")
        @Size(max = 255, message = "O email deve ter no máximo 255 caracteres")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, max = 50, message = "A senha deve ter entre 6 e 50 caracteres")
        String senha
    ) {}

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
}