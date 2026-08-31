package com.migopan.api.dto;

import com.migopan.api.model.Tarefa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TarefaDTOs {

    public record CriarTarefaRequestDTO(
        @NotBlank(message = "O título é obrigatório")
        @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
        String titulo,
        
        String descricao,
        
        // ("NENHUMA", "DIARIA", "SEMANAL", "MENSAL")
        String repeticao,
        
        // Se enviado, é tarefa do grupo. Se null, é tarefa pessoal.
        Long grupoId
    ) {}

    public record AtualizarTarefaRequestDTO(
        @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
        String titulo,
        
        String descricao,
        
        String repeticao
    ) {}

    public record TarefaResponseDTO(
        Long id,
        String titulo,
        String descricao,
        String repeticao,
        LocalTime horarioResolucao,
        LocalDateTime dataCriacao,
        LocalDateTime dataResolucao,
        Boolean concluida,
        Long grupoId,
        Long usuarioCriadorId,
        Long usuarioAtribuidoId
    ) {
        public TarefaResponseDTO(Tarefa tarefa) {
            this(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getRepeticao(),
                tarefa.getHorarioResolucao(),
                tarefa.getDataCriacao(),
                tarefa.getDataResolucao(),
                tarefa.getConcluida(),
                tarefa.getGrupo() != null ? tarefa.getGrupo().getId() : null,
                tarefa.getUsuarioCriador().getId(),
                tarefa.getUsuarioAtribuido() != null ? tarefa.getUsuarioAtribuido().getId() : null
            );
        }
    }
}