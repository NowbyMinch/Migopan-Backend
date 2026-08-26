package com.migopan.api.dto;

import com.migopan.api.model.GrupoMembro;
import java.time.LocalDateTime;

public record GrupoMembroResponseDTO(
        Long usuarioId,
        String nomeUsuario,
        String email,
        String papel,
        LocalDateTime dataEntrada,
        Boolean bloqueado,
        GrupoResponseDTO grupo 
){
    public GrupoMembroResponseDTO(GrupoMembro grupoMembro) {
        this(
                grupoMembro.getUsuario().getId(),
                grupoMembro.getUsuario().getNome(),
                grupoMembro.getUsuario().getEmail(),
                grupoMembro.getPapel(),
                grupoMembro.getDataEntrada(),
                grupoMembro.getBloqueado(),
                null 
        );
    }

    public GrupoMembroResponseDTO(GrupoMembro grupoMembro, long totalMembrosNoGrupo) {
        this(
                grupoMembro.getUsuario().getId(),
                grupoMembro.getUsuario().getNome(),
                grupoMembro.getUsuario().getEmail(),
                grupoMembro.getPapel(),
                grupoMembro.getDataEntrada(),
                grupoMembro.getBloqueado(),
                new GrupoResponseDTO(grupoMembro.getGrupo(), totalMembrosNoGrupo)
        );
    }
}