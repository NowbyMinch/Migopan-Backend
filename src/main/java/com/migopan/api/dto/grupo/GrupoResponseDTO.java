package com.migopan.api.dto;

import java.time.LocalDateTime;

import com.migopan.api.model.Grupo;

public record GrupoResponseDTO(
    Long id,
    String nome,
    String descricao,
    Long quantidadeMembros
) {
    public GrupoResponseDTO(Grupo grupo) {
        this(grupo.getId(), grupo.getNome(), grupo.getDescricao(), quantidadeMembros);
    }
}