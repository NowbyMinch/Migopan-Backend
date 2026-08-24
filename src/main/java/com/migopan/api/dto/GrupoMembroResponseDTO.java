package com.migopan.api.dto;

import com.migopan.api.model.GrupoMembro;
import java.time.LocalDateTime;

public record GrupoMembroResponseDTO(
        Long usuarioId,
        String nomeUsuario,
        String papel,
        LocalDateTime dataEntrada,
        Boolean bloqueado
){
    public GrupoMembroResponseDTO(GrupoMembro grupoMembro) {
        this(
                grupoMembro.getUsuario().getId(),
                grupoMembro.getUsuario().getNome(),
                grupoMembro.getUsuario().getEmail(),
                grupoMembro.getPapel(),
                grupoMembro.getDataEntrada(),
                grupoMembro.getBloqueado()
        );
    }
}