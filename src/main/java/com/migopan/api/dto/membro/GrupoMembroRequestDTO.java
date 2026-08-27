package com.migopan.api.dto.membro;

import jakarta.validation.constraints.NotNull;

public record GrupoMembroRequestDTO(
        @NotNull(message = "O ID do grupo não pode ser nulo")
        Long usuarioId,

        String papel
) {}