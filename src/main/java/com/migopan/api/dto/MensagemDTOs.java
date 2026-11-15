package com.migopan.api.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.migopan.api.model.Mensagem;

public final class MensagemDTOs {

    private MensagemDTOs() {
    }

    public record EnviarMensagemRequestDTO(String texto) {
    }

    public record MensagemResponseDTO(
            Long id,
            Long grupoId,
            Long usuarioId,
            String nomeUsuario,
            String texto,
            @JsonFormat(shape = JsonFormat.Shape.STRING) LocalDateTime dataEnvio) {

        public MensagemResponseDTO(Mensagem m) {
            this(m.getId(), m.getGrupo().getId(), m.getUsuario().getId(), m.getUsuario().getNome(),
                    m.getTexto(), m.getDataEnvio());
        }
    }

    public record ErroChatDTO(String mensagem) {
    }
}
