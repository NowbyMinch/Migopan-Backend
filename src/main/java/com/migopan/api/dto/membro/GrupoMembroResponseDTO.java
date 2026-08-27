package com.migopan.api.dto.membro;

import com.migopan.api.model.GrupoMembro;
import java.time.LocalDateTime;

public record GrupoMembroResponseDTO(
        Long usuarioId,
        String nomeUsuario,
        String emailUsuario,
        String papel,
        boolean bloqueado,
        LocalDateTime dataEntrada) {
    public GrupoMembroResponseDTO(GrupoMembro membro) {
        this(
                membro.getUsuario().getId(),
                membro.getUsuario().getNome(),
                membro.getUsuario().getEmail(),
                membro.getPapel(),
                membro.getBloqueado(),
                membro.getDataEntrada());
    }
}

// package com.migopan.api.dto.membro;

// import com.migopan.api.dto.grupo.*;
// import com.migopan.api.model.GrupoMembro;
// import java.time.LocalDateTime;

// public record GrupoMembroResponseDTO(
//         Long usuarioId,
//         String nomeUsuario,
//         // String email,
//         String papel,
//         LocalDateTime dataEntrada,
//         Boolean bloqueado,
//         GrupoResponseDTO grupo 
// ){
//     public GrupoMembroResponseDTO(GrupoMembro grupoMembro) {
//         this(
//                 grupoMembro.getUsuario().getId(),
//                 grupoMembro.getUsuario().getNome(),
//                 grupoMembro.getPapel(),
//                 grupoMembro.getDataEntrada(),
//                 grupoMembro.getBloqueado(),
//                 new GrupoResponseDTO(grupoMembro.getGrupo(), 1L)
//         );
//     }

//     public GrupoMembroResponseDTO(GrupoMembro grupoMembro, long totalMembrosNoGrupo) {
//         this(
//                 grupoMembro.getUsuario().getId(),
//                 grupoMembro.getUsuario().getNome(),
//                 grupoMembro.getPapel(),
//                 grupoMembro.getDataEntrada(),
//                 grupoMembro.getBloqueado(),
//                 new GrupoResponseDTO(grupoMembro.getGrupo(), totalMembrosNoGrupo)
//         );
//     }
// }