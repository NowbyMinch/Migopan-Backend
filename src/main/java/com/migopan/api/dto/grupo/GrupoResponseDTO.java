package com.migopan.api.dto.grupo;

import com.migopan.api.model.Grupo;

public record GrupoResponseDTO(
    Long id,
    String nome,
    String descricao,
    Long quantidadeMembros
) {
    public GrupoResponseDTO(Grupo grupo) {
        this(grupo.getId(), grupo.getNome(), grupo.getDescricao(), 1L);
    }

    public GrupoResponseDTO(Grupo grupo, Long quantidadeMembros) {
        this(grupo.getId(), grupo.getNome(), grupo.getDescricao(), quantidadeMembros);
    }
}