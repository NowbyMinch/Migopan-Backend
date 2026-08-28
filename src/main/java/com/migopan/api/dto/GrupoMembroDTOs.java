package com.migopan.api.dto;

import com.migopan.api.model.GrupoMembro;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

public class GrupoMembroDTOs {

    public record AtualizarMembroRequestDTO(
        @Pattern(regexp = "ADMIN|MEMBRO", message = "O papel deve ser 'ADMIN' ou 'MEMBRO'")
        String papel,
        
        Boolean bloqueado
    ) {}

    public record GrupoMembroRequestDTO(
        @NotNull(message = "O ID do usuário não pode ser nulo")
        Long usuarioId,

        String papel
    ) {}

    public record GrupoMembroResponseDTO(
        Long usuarioId,
        String nomeUsuario,
        String emailUsuario,
        String papel,
        boolean bloqueado,
        LocalDateTime dataEntrada
    ) {
        public GrupoMembroResponseDTO(GrupoMembro membro) {
            this(
                membro.getUsuario().getId(),
                membro.getUsuario().getNome(),
                membro.getUsuario().getEmail(),
                membro.getPapel(),
                membro.getBloqueado(),
                membro.getDataEntrada()
            );
        }
    }
}