package com.migopan.api.dto;

import java.time.LocalDateTime;

import com.migopan.api.model.GrupoMembro;
import com.migopan.api.model.enums.PapelGrupo;

import jakarta.validation.constraints.NotNull;

public final class GrupoMembroDTOs {

    private GrupoMembroDTOs() {
    }

    public record GrupoMembroRequestDTO(
            @NotNull(message = "O ID do usuário não pode ser nulo")
            Long usuarioId,

            PapelGrupo papel) {
    }

    public record AtualizarMembroRequestDTO(PapelGrupo papel, Boolean bloqueado) {
    }

    public record GrupoMembroResponseDTO(
            Long usuarioId,
            String nomeUsuario,
            String emailUsuario,
            PapelGrupo papel,
            boolean bloqueado,
            LocalDateTime dataEntrada) {

        public GrupoMembroResponseDTO(GrupoMembro m) {
            this(m.getUsuario().getId(), m.getUsuario().getNome(), m.getUsuario().getEmail(),
                    m.getPapel(), m.getBloqueado(), m.getDataEntrada());
        }
    }
}
