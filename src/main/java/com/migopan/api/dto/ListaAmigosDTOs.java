package com.migopan.api.dto;

import java.time.LocalDateTime;

import com.migopan.api.model.ListaAmigos;
import com.migopan.api.model.Usuario;
import com.migopan.api.model.enums.StatusAmizade;

import jakarta.validation.constraints.NotNull;

public final class ListaAmigosDTOs {

    private ListaAmigosDTOs() {
    }

    public record AtualizarStatusAmizadeRequestDTO(
            @NotNull(message = "O status é obrigatório")
            StatusAmizade status) {
    }

    public record UsuarioAmigoResponseDTO(Long id, String nome) {
        public UsuarioAmigoResponseDTO(Usuario u) {
            this(u.getId(), u.getNome());
        }
    }

    public record AmizadeResponseDTO(
            Long usuarioId,
            UsuarioAmigoResponseDTO amigo,
            StatusAmizade statusAmizade,
            LocalDateTime dataAmizade) {

        public AmizadeResponseDTO(ListaAmigos relacao, Long usuarioLogadoId) {
            this(usuarioLogadoId,
                    new UsuarioAmigoResponseDTO(
                            relacao.getUsuario().getId().equals(usuarioLogadoId)
                                    ? relacao.getAmigo()
                                    : relacao.getUsuario()),
                    relacao.getStatusAmizade(),
                    relacao.getDataAmizade());
        }
    }
}
