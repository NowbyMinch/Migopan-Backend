package com.migopan.api.dto;

import java.time.LocalDateTime;

import com.migopan.api.model.Group;

public record GroupResponseDTO(
    Long id,
    String nome,
    String descricao,
    Boolean ativo, 
    LocalDateTime dataCriacao
) {
    public GroupResponseDTO(Group group) {
        this(group.getId(), group.getNome(), group.getDescricao(), group.getAtivo(), group.getDataCriacao());
    }
}