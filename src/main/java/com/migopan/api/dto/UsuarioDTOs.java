package com.migopan.api.dto;

import java.math.BigDecimal;

import com.migopan.api.model.Usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class UsuarioDTOs {

    private UsuarioDTOs() {
    }

    public record UsuarioRequestDTO(
            @NotBlank(message = "O nome é obrigatório")
            @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
            String nome,

            @NotBlank(message = "O email é obrigatório")
            @Email(message = "Formato de email inválido")
            @Size(max = 150, message = "O email deve ter no máximo 150 caracteres")
            String email,

            @NotBlank(message = "A senha é obrigatória")
            @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres")
            String senha) {
    }

    public record AtualizarUsuarioRequestDTO(
            @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
            String nome,

            @Email(message = "Formato de email inválido")
            @Size(max = 150, message = "O email deve ter no máximo 150 caracteres")
            String email) {
    }

    public record PerfilResponseDTO(Long id, String nome, Boolean ativo) {
        public PerfilResponseDTO(Usuario u) {
            this(u.getId(), u.getNome(), u.getAtivo());
        }
    }

    public record UsuarioResponseDTO(
            Long id,
            String nome,
            String email,
            Boolean emailVerificado,
            Boolean ativo,
            Integer sequenciaDias,
            BigDecimal dinheiro) {
        public UsuarioResponseDTO(Usuario u) {
            this(u.getId(), u.getNome(), u.getEmail(), u.getEmailVerificado(),
                    u.getAtivo(), u.getSequenciaDias(), u.getDinheiro());
        }
    }
}
