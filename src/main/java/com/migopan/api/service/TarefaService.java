package com.migopan.api.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.migopan.api.dto.TarefaDTOs.AtualizarTarefaRequestDTO;
import com.migopan.api.dto.TarefaDTOs.CriarTarefaRequestDTO;
import com.migopan.api.dto.TarefaDTOs.TarefaResponseDTO;
import com.migopan.api.exception.AcessoNegadoException;
import com.migopan.api.exception.NotFoundException;
import com.migopan.api.model.Grupo;
import com.migopan.api.model.Tarefa;
import com.migopan.api.model.Usuario;
import com.migopan.api.model.enums.Repeticao;
import com.migopan.api.repository.GrupoMembroRepository;
import com.migopan.api.repository.GrupoRepository;
import com.migopan.api.repository.TarefaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final GrupoRepository grupoRepository;
    private final GrupoMembroRepository grupoMembroRepository;

    @Transactional
    public TarefaResponseDTO criarTarefa(Usuario usuarioLogado, CriarTarefaRequestDTO dto) {
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(dto.titulo().trim());
        tarefa.setDescricao(dto.descricao());
        tarefa.setCategoria(dto.categoria());
        tarefa.setCor(dto.cor());
        tarefa.setPrioridade(Boolean.TRUE.equals(dto.prioridade()));
        tarefa.setRepeticao(dto.repeticao() != null ? dto.repeticao() : Repeticao.NENHUMA);
        tarefa.setDataLimite(dto.dataLimite());
        tarefa.setHorarioLimite(dto.horarioLimite());
        tarefa.setUsuarioCriador(usuarioLogado);
        tarefa.setConcluida(false);

        if (dto.grupoId() != null) {
            Grupo grupo = grupoRepository.findById(dto.grupoId())
                    .orElseThrow(() -> new NotFoundException("Grupo não encontrado."));
            exigirMembro(grupo.getId(), usuarioLogado, "Você não pertence a este grupo.");
            tarefa.setGrupo(grupo);
        } else {
            tarefa.setUsuarioAtribuido(usuarioLogado);
        }

        return new TarefaResponseDTO(tarefaRepository.save(tarefa));
    }

    public List<TarefaResponseDTO> listarTarefasPessoais(Usuario usuarioLogado, Boolean concluida) {
        List<Tarefa> tarefas = concluida != null
                ? tarefaRepository.findByUsuarioAtribuidoIdAndConcluida(usuarioLogado.getId(), concluida)
                : tarefaRepository.findByUsuarioAtribuidoId(usuarioLogado.getId());
        return tarefas.stream().map(TarefaResponseDTO::new).toList();
    }

    public List<TarefaResponseDTO> listarTarefasPorGrupo(Long grupoId, Usuario usuarioLogado, Boolean concluida) {
        if (!grupoRepository.existsById(grupoId)) {
            throw new NotFoundException("Grupo não encontrado.");
        }
        exigirMembro(grupoId, usuarioLogado, "Você não tem acesso às tarefas deste grupo.");

        List<Tarefa> tarefas = concluida != null
                ? tarefaRepository.findByGrupoIdAndConcluida(grupoId, concluida)
                : tarefaRepository.findByGrupoId(grupoId);
        return tarefas.stream().map(TarefaResponseDTO::new).toList();
    }

    public TarefaResponseDTO buscarPorId(Long tarefaId, Usuario usuarioLogado) {
        return new TarefaResponseDTO(buscarComAcesso(tarefaId, usuarioLogado));
    }

    @Transactional
    public TarefaResponseDTO alterarConclusao(Long tarefaId, Usuario usuarioLogado) {
        Tarefa tarefa = buscarComAcesso(tarefaId, usuarioLogado);

        boolean novoStatus = !Boolean.TRUE.equals(tarefa.getConcluida());
        tarefa.setConcluida(novoStatus); 
        tarefa.setDataResolucao(novoStatus ? LocalDateTime.now() : null);
        tarefa.setHorarioResolucao(novoStatus ? LocalTime.now() : null);

        return new TarefaResponseDTO(tarefaRepository.save(tarefa));
    }

    @Transactional
    public TarefaResponseDTO atualizarTarefa(Long tarefaId, Usuario usuarioLogado, AtualizarTarefaRequestDTO dto) {
        Tarefa tarefa = buscarComAcesso(tarefaId, usuarioLogado);

        if (dto.titulo() != null && !dto.titulo().isBlank()) {
            tarefa.setTitulo(dto.titulo().trim());
        }
        if (dto.descricao() != null) {
            tarefa.setDescricao(dto.descricao());
        }
        if (dto.categoria() != null) {
            tarefa.setCategoria(dto.categoria());
        }
        if (dto.cor() != null) {
            tarefa.setCor(dto.cor());
        }
        if (dto.prioridade() != null) { 
            tarefa.setPrioridade(dto.prioridade());
        }
        if (dto.repeticao() != null) {
            tarefa.setRepeticao(dto.repeticao());
        }
        if (dto.dataLimite() != null) {
            tarefa.setDataLimite(dto.dataLimite());
        }
        if (dto.horarioLimite() != null) {
            tarefa.setHorarioLimite(dto.horarioLimite());
        }

        return new TarefaResponseDTO(tarefaRepository.save(tarefa));
    }

    @Transactional
    public void deletarTarefa(Long tarefaId, Usuario usuarioLogado) {
        tarefaRepository.delete(buscarComAcesso(tarefaId, usuarioLogado));
    }

    // ---------- apoio ----------

    private Tarefa buscarComAcesso(Long tarefaId, Usuario usuario) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new NotFoundException("Tarefa não encontrada."));

        if (tarefa.getGrupo() != null) {
            exigirMembro(tarefa.getGrupo().getId(), usuario, "Você não tem acesso às tarefas deste grupo.");
        } else {
            boolean ehAtribuido = tarefa.getUsuarioAtribuido() != null
                    && tarefa.getUsuarioAtribuido().getId().equals(usuario.getId());
            boolean ehCriador = tarefa.getUsuarioCriador().getId().equals(usuario.getId());
            if (!ehAtribuido && !ehCriador) {
                throw new AcessoNegadoException("Você não tem acesso a esta tarefa pessoal.");
            }
        }
        return tarefa;
    }

    private void exigirMembro(Long grupoId, Usuario usuario, String mensagem) {
        if (!grupoMembroRepository.existsByGrupoIdAndUsuarioIdAndBloqueadoFalse(grupoId, usuario.getId())) {
            throw new AcessoNegadoException(mensagem);
        }
    }
}
