package com.migopan.api.dto;

import com.migopan.api.model.Grupo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime; // <-- IMPORT ADICIONADO

public class GrupoDTOs {

    public record AtualizarGrupoRequestDTO(
        @Size(min = 2, max = 100, message = "O nome do grupo deve ter entre 2 e 100 caracteres")
        String nome,
        
        String descricao
    ) {}

    public record GrupoRequestDTO(
        @NotBlank(message = "O nome é obrigatório")
        String nome,
        
        String descricao
    ) {}

    public record GrupoResponseDTO(
        Long id,
        String nome,
        String descricao,
        Long quantidadeMembros,
        Long quantidadeAtividades,
        Integer nivel, 
        Integer sequenciaDias,
        LocalDateTime dataCriacao
    ) {
        public GrupoResponseDTO(Grupo grupo) {
            this(
                grupo.getId(),
                grupo.getNome(),
                grupo.getDescricao(),
                1L,
                0L,
                grupo.getNivel() != null ? grupo.getNivel() : 1,
                grupo.getSequenciaDias() != null ? grupo.getSequenciaDias() : 0,
                grupo.getDataCriacao()
            );
        }

        public GrupoResponseDTO(Grupo grupo, Long quantidadeMembros, long quantidadeAtividades) {
            this(
                grupo.getId(),
                grupo.getNome(),
                grupo.getDescricao(),
                quantidadeMembros != null ? quantidadeMembros : 1L,
                quantidadeAtividades,
                grupo.getNivel() != null ? grupo.getNivel() : 1,
                grupo.getSequenciaDias() != null ? grupo.getSequenciaDias() : 0,
                grupo.getDataCriacao()
            );
        }
    }
}