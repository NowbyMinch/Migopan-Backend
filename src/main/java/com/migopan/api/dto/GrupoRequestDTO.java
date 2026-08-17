package com.migopan.api.dto;

import jakarta.validation.constraints.NotBlank;

public record GrupoRequestDTO(
    @NotBlank(message="O nome é obrigatório")
    String nome,
    String descricao
) {}
