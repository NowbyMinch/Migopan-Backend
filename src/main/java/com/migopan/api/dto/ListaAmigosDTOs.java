package com.migopan.api.dto;

import com.migopan.api.model.Grupo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ListaAmigos {
    // @Pattern(regexp = "ADMIN|MEMBRO", message = "O papel deve ser 'ADMIN' ou 'MEMBRO'")

    public record AtualizarStatusAmizadeRequestDTO(
        @NotBlank(message = "O status é obrigatório")
        @Pattern(regexp = "ACEITA|RECUSADA", message = "O status deve ser 'ACEITA' ou 'RECUSADA'")
        String status
    ) {}


    public record UsuarioAmigoResponseDTO(
        Long id,
        String nome,
    ) {
        public UsuarioAmigoResponseDTO(Usuario usuario) {
            this(usuario.getId(), usuario.getNome());
        };
    }

    public record AmizadeResponseDTO(
        Long usuarioId,
        UsuarioAmigoResponseDTO amigo,
        String statusAmizade,
        LocalDateTime dataAmizade
    ) {
        public AmizadeResponseDTO(ListaAmigos relacao, Long usuarioLogadoId){
            boolean souOuvinte = relacao.getUsuario().getId().equals(usuarioId);
            Usuario outroUsuario = souOuvinte ? relacao.getAmigo() : relacao.getUsuario(); 

            this (
                usuarioLogadoId,
                new UsuarioAmigoResponseDTO(outroUsuario),
                relacao.getStatusAmizade(),
                relacao.getDataAmizade()
            );
        } 

    }

}