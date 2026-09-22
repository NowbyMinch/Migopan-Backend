package com.migopan.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.migopan.api.model.Tarefa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TarefaDTOs {

    public record CriarTarefaRequestDTO(
        @NotBlank(message = "O título é obrigatório")
        @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
        String titulo,
        
        String descricao,
                
        String categoria,
        String cor,
        Boolean prioridade,
                    
        // ("NENHUMA", "DIARIA", "SEMANAL", "MENSAL")
        String repeticao,
        
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dataLimite,
                
        @JsonFormat(pattern = "HH:mm")
        LocalTime horarioLimite,

        // Se enviado, é tarefa do grupo. Se null, é tarefa pessoal.
        Long grupoId,
        
        Long usuarioAtribuidoId

    ) {}

    public record AtualizarTarefaRequestDTO(
        @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
        String titulo,
        String descricao,
        
        String categoria,
        String cor,
        Boolean prioridade,
        
        String repeticao,
        
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dataLimite,
        
        @JsonFormat(pattern = "HH:mm")
        LocalTime horarioLimite,
            
        LocalTime horarioResolucao
    ) {}

    public record TarefaResponseDTO(
        Long id,
        String titulo,
        String descricao,
        String categoria,
        String cor,
        Boolean prioridade,
        String repeticao,
        LocalDate dataLimite,
        LocalTime horarioLimite,
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
                tarefa.getCategoria(),
                tarefa.getCor(),
                tarefa.getPrioridade(),
                tarefa.getRepeticao(),
                tarefa.getDataLimite(),
                tarefa.getHorarioLimite(),
                tarefa.getHorarioResolucao(),
                tarefa.getDataCriacao(),
                tarefa.getDataResolucao(),
                tarefa.getConcluida(),
                tarefa.getGrupo() != null ? tarefa.getGrupo().getId() : null,
                tarefa.getUsuarioCriador() != null ? tarefa.getUsuarioCriador().getId() : null,
                tarefa.getUsuarioAtribuido() != null ? tarefa.getUsuarioAtribuido().getId() : null
            );
        }
    }
}