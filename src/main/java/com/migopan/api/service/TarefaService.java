package com.migopan.api.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.migopan.api.dto.TarefaDTOs.AtualizarTarefaRequestDTO;
import com.migopan.api.dto.TarefaDTOs.CriarTarefaRequestDTO;
import com.migopan.api.dto.TarefaDTOs.TarefaResponseDTO;
import com.migopan.api.exception.AcessoNegadoException;
import com.migopan.api.exception.NotFoundException;
import com.migopan.api.model.Grupo;
import com.migopan.api.model.Tarefa;
import com.migopan.api.model.Usuario;
import com.migopan.api.repository.GrupoMembroRepository;
import com.migopan.api.repository.GrupoRepository;
import com.migopan.api.repository.TarefaRepository;

import jakarta.transaction.Transactional;

@Service 
public class TarefaService {
    @Autowired
    public TarefaRepository tarefaRepository;
    
    @Autowired
    public GrupoRepository grupoRepository;
    
    @Autowired
    public GrupoMembroRepository grupoMembroRepository;

    private void validarAcessoTarefa(Tarefa tarefa, Usuario usuario) {
        if (tarefa.getUsuarioAtribuido() != null) {
            if (!tarefa.getUsuarioAtribuido().getId().equals(usuario.getId())
                    && !tarefa.getUsuarioCriador().getId().equals(usuario.getId())) {
                throw new AcessoNegadoException("Você não tem acesso a esta tarefa pessoal.");
            }
        }
        else if (tarefa.getGrupo() != null) {
            if (!grupoMembroRepository.existsByGrupoIdAndUsuarioId(tarefa.getGrupo().getId(), usuario.getId())) {
                throw new AcessoNegadoException("Você não tem acesso às tarefas deste grupo.");
            }
        }
    }

    @Transactional
    public TarefaResponseDTO criarTarefa(Usuario usuarioLogado, CriarTarefaRequestDTO dto) {
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(dto.titulo());
        tarefa.setDescricao(dto.descricao());
        tarefa.setRepeticao(dto.repeticao() != null && !dto.repeticao().isBlank() ? dto.repeticao() : "NENHUMA");
        tarefa.setUsuarioCriador(usuarioLogado);
        tarefa.setConcluida(false);

        if (dto.grupoId() != null) {
            Grupo grupo = grupoRepository.findById(dto.grupoId())
                    .orElseThrow(() -> new NotFoundException("Grupo não encontrado."));

            if (!grupoMembroRepository.existsByGrupoIdAndUsuarioId(dto.grupoId(), usuarioLogado.getId())) {
                throw new AcessoNegadoException("Você não pertence a este grupo.");
            }

            tarefa.setGrupo(grupo);
            tarefa.setUsuarioAtribuido(null);
        } else {
            tarefa.setGrupo(null);
            tarefa.setUsuarioAtribuido(usuarioLogado);
        }

        Tarefa salva = tarefaRepository.save(tarefa);
        return new TarefaResponseDTO(salva);
    }
    
    public List<TarefaResponseDTO> listarTarefasPessoais(Usuario usuarioLogado, Boolean concluida) {
        List<Tarefa> tarefas;

        if (concluida != null) {
            tarefas = tarefaRepository.findByUsuarioAtribuidoIdAndConcluida(usuarioLogado.getId(), concluida);
        } else {
            tarefas = tarefaRepository.findByUsuarioAtribuidoId(usuarioLogado.getId());
        }

        return tarefas.stream().map(TarefaResponseDTO::new).toList();
    }
    
    public List<TarefaResponseDTO> listarTarefasPorGrupo(Long grupoId, Usuario usuarioLogado, Boolean concluida) {
        if (!grupoRepository.existsById(grupoId)) {
            throw new NotFoundException("Grupo não encontrado.");
        }

        if (!grupoMembroRepository.existsByGrupoIdAndUsuarioId(grupoId, usuarioLogado.getId())) {
            throw new AcessoNegadoException("Você não tem acesso às tarefas deste grupo.");
        }

        List<Tarefa> tarefas;

        if (concluida != null) {
            tarefas = tarefaRepository.findByGrupoIdAndConcluida(grupoId, concluida);
        } else {
            tarefas = tarefaRepository.findByGrupoId(grupoId);
        }

        return tarefas.stream().map(TarefaResponseDTO::new).toList();
    }

    public TarefaResponseDTO buscarPorId(Long tarefaId, Usuario usuarioLogado) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new NotFoundException("Tarefa não encontrada."));

        validarAcessoTarefa(tarefa, usuarioLogado);

        return new TarefaResponseDTO(tarefa);
    }

    @Transactional
    public TarefaResponseDTO AlterarConclusao(Long tarefaId, Usuario usuarioLogado) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new NotFoundException("Tarefa não encontrada."));

        validarAcessoTarefa(tarefa, usuarioLogado);

        Boolean novoStatus = !tarefa.getConcluida();

        if (novoStatus) {
            tarefa.setDataResolucao(LocalDateTime.now());
            tarefa.setHorarioResolucao(LocalTime.now());
        } else {
            tarefa.setDataResolucao(null);
            tarefa.setHorarioResolucao(null);
        }

        Tarefa salva = tarefaRepository.save(tarefa);
        return new TarefaResponseDTO(salva);
    }
    
    @Transactional
    public TarefaResponseDTO atualizarTarefa(Long tarefaId, Usuario usuarioLogado, AtualizarTarefaRequestDTO dto) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new NotFoundException("Tarefa não encontrada."));

        validarAcessoTarefa(tarefa, usuarioLogado);

        if (dto.titulo() != null && !dto.titulo().isBlank()) {
            tarefa.setTitulo(dto.titulo());
        }
        if (dto.descricao() != null) {
            tarefa.setDescricao(dto.descricao());
        }
        if (dto.repeticao() != null && !dto.repeticao().isBlank()) {
            tarefa.setRepeticao(dto.repeticao());
        }

        Tarefa salva = tarefaRepository.save(tarefa);
        return new TarefaResponseDTO(salva);
    }
    
    @Transactional 
    public void deletarTarefa(Long tarefaId, Usuario usuarioLogado) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new NotFoundException("Tarefa não encontrada."));

        validarAcessoTarefa(tarefa, usuarioLogado);

        tarefaRepository.delete(tarefa);
    }
    
}