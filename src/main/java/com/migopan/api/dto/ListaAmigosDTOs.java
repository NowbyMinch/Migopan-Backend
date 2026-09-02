package com.migopan.api.dto;

import java.time.LocalDateTime;

import com.migopan.api.model.ListaAmigos; // Import correto da entidade
import com.migopan.api.model.Usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ListaAmigosDTOs {

    public record AtualizarStatusAmizadeRequestDTO(
        @NotBlank(message = "O status é obrigatório")
        @Pattern(regexp = "ACEITA|RECUSADA", message = "O status deve ser 'ACEITA' ou 'RECUSADA'")
        String status
    ) {}

    public record UsuarioAmigoResponseDTO(
        Long id,
        String nome
    ) {
        public UsuarioAmigoResponseDTO(Usuario usuario) {
            this(usuario.getId(), usuario.getNome());
        }
    }

    public record AmizadeResponseDTO(
        Long usuarioId,
        UsuarioAmigoResponseDTO amigo,
        String statusAmizade,
        LocalDateTime dataAmizade
    ) {
        // Recebe a ENTIDADE ListaAmigos, não o DTO
        public AmizadeResponseDTO(ListaAmigos relacao, Long usuarioLogadoId) {
            this(
                usuarioLogadoId,
                new UsuarioAmigoResponseDTO(
                    relacao.getUsuario().getId().equals(usuarioLogadoId) 
                        ? relacao.getAmigo() 
                        : relacao.getUsuario()
                ),
                relacao.getStatusAmizade(),
                relacao.getDataAmizade()
            );
        }
    }
}