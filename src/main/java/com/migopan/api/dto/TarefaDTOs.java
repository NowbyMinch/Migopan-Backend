package com.migopan.api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.migopan.api.model.Tarefa;
import com.migopan.api.model.enums.Repeticao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class TarefaDTOs {

    private TarefaDTOs() {
    }

    public record CriarTarefaRequestDTO(
            @NotBlank(message = "O título é obrigatório")
            @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
            String titulo,

            String descricao,

            @Size(max = 50, message = "A categoria deve ter no máximo 50 caracteres")
            String categoria,

            @Size(max = 20, message = "A cor deve ter no máximo 20 caracteres")
            String cor,

            Boolean prioridade,

            Repeticao repeticao,

            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDate dataLimite,

            @JsonFormat(pattern = "HH:mm")
            LocalTime horarioLimite,

            Long grupoId) {
    }

    public record AtualizarTarefaRequestDTO(
            @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
            String titulo,

            String descricao,

            @Size(max = 50, message = "A categoria deve ter no máximo 50 caracteres")
            String categoria,

            @Size(max = 20, message = "A cor deve ter no máximo 20 caracteres")
            String cor,

            Boolean prioridade,

            Repeticao repeticao,

            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDate dataLimite,

            @JsonFormat(pattern = "HH:mm")
            LocalTime horarioLimite) {
    }

    public record TarefaResponseDTO(
            Long id,
            String titulo,
            String descricao,
            String categoria,
            String cor,
            Boolean prioridade,
            Repeticao repeticao,
            LocalDate dataLimite,
            LocalTime horarioLimite,
            LocalTime horarioResolucao,
            LocalDateTime dataCriacao,
            LocalDateTime dataResolucao,
            Boolean concluida,
            Long grupoId,
            Long usuarioCriadorId,
            Long usuarioAtribuidoId) {

        public TarefaResponseDTO(Tarefa t) {
            this(t.getId(), t.getTitulo(), t.getDescricao(), t.getCategoria(), t.getCor(),
                    t.getPrioridade(), t.getRepeticao(), t.getDataLimite(), t.getHorarioLimite(),
                    t.getHorarioResolucao(), t.getDataCriacao(), t.getDataResolucao(), t.getConcluida(),
                    t.getGrupo() != null ? t.getGrupo().getId() : null,
                    t.getUsuarioCriador() != null ? t.getUsuarioCriador().getId() : null,
                    t.getUsuarioAtribuido() != null ? t.getUsuarioAtribuido().getId() : null);
        }
    }
}
