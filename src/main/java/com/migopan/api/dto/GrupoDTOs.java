package com.migopan.api.dto;

import java.time.LocalDateTime;

import com.migopan.api.model.Grupo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class GrupoDTOs {

    private GrupoDTOs() {
    }

    public record GrupoRequestDTO(
            @NotBlank(message = "O nome é obrigatório")
            @Size(max = 100, message = "O nome do grupo deve ter no máximo 100 caracteres")
            String nome,

            String descricao) {
    }

    public record AtualizarGrupoRequestDTO(
            @Size(min = 2, max = 100, message = "O nome do grupo deve ter entre 2 e 100 caracteres")
            String nome,

            String descricao) {
    }

    public record GrupoResponseDTO(
            Long id,
            String nome,
            String descricao,
            Long quantidadeMembros,
            Long quantidadeAtividades,
            Integer nivel,
            Integer sequenciaDias,
            LocalDateTime dataCriacao) {

        public GrupoResponseDTO(Grupo g, long quantidadeMembros, long quantidadeAtividades) {
            this(g.getId(), g.getNome(), g.getDescricao(),
                    quantidadeMembros, quantidadeAtividades,
                    g.getNivel() != null ? g.getNivel() : 1,
                    g.getSequenciaDias() != null ? g.getSequenciaDias() : 0,
                    g.getDataCriacao());
        }
    }
}
