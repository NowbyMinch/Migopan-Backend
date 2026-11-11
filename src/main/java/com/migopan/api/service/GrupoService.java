package com.migopan.api.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.migopan.api.dto.GrupoDTOs.AtualizarGrupoRequestDTO;
import com.migopan.api.dto.GrupoDTOs.GrupoRequestDTO;
import com.migopan.api.dto.GrupoDTOs.GrupoResponseDTO;
import com.migopan.api.exception.AcessoNegadoException;
import com.migopan.api.exception.NotFoundException;
import com.migopan.api.model.Grupo;
import com.migopan.api.model.GrupoMembro;
import com.migopan.api.model.Usuario;
import com.migopan.api.model.enums.PapelGrupo;
import com.migopan.api.model.keys.GrupoMembroId;
import com.migopan.api.repository.ContagemPorGrupo;
import com.migopan.api.repository.GrupoMembroRepository;
import com.migopan.api.repository.GrupoRepository;
import com.migopan.api.repository.TarefaRepository;
import com.migopan.api.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GrupoService {

    private final GrupoRepository grupoRepository;
    private final UsuarioRepository usuarioRepository;
    private final GrupoMembroRepository grupoMembroRepository;
    private final TarefaRepository tarefaRepository;

    public List<GrupoResponseDTO> listarDoUsuario(Long usuarioId) {
        return montarRespostas(grupoMembroRepository.findGruposByUsuarioId(usuarioId));
    }

    public GrupoResponseDTO buscarPorId(Long grupoId, Usuario usuarioLogado) {
        Grupo grupo = buscar(grupoId);
        if (!grupoMembroRepository.existsByGrupoIdAndUsuarioIdAndBloqueadoFalse(grupoId, usuarioLogado.getId())) {
            throw new AcessoNegadoException("Você não tem acesso a este grupo.");
        }
        return montarRespostas(List.of(grupo)).get(0);
    }

    @Transactional
    public GrupoResponseDTO criarGrupo(GrupoRequestDTO dto, Usuario usuarioLogado) {
        Grupo grupo = new Grupo();
        grupo.setNome(dto.nome().trim());
        grupo.setDescricao(dto.descricao());
        grupo.setNivel(1);
        grupo.setSequenciaDias(0);
        Grupo salvo = grupoRepository.save(grupo);

        GrupoMembro membro = new GrupoMembro();
        membro.setId(new GrupoMembroId(salvo.getId(), usuarioLogado.getId()));
        membro.setGrupo(salvo);
        membro.setUsuario(usuarioRepository.getReferenceById(usuarioLogado.getId()));
        membro.setPapel(PapelGrupo.ADMIN);
        membro.setBloqueado(false);
        grupoMembroRepository.save(membro);

        return new GrupoResponseDTO(salvo, 1, 0);
    }

    @Transactional
    public GrupoResponseDTO atualizarGrupo(Long grupoId, Usuario usuarioLogado, AtualizarGrupoRequestDTO dto) {
        Grupo grupo = buscar(grupoId);
        exigirAdmin(grupoId, usuarioLogado, "Apenas administradores podem editar o grupo.");

        boolean temNome = dto.nome() != null && !dto.nome().isBlank();
        if (!temNome && dto.descricao() == null) {
            throw new IllegalArgumentException("Nenhum dado foi fornecido para atualização.");
        }

        if (temNome) {
            grupo.setNome(dto.nome().trim());
        }
        if (dto.descricao() != null) {
            grupo.setDescricao(dto.descricao());
        }

        return montarRespostas(List.of(grupoRepository.save(grupo))).get(0);
    }

    @Transactional
    public void deletarGrupo(Long grupoId, Usuario usuarioLogado) {
        buscar(grupoId);
        exigirAdmin(grupoId, usuarioLogado, "Apenas administradores podem deletar o grupo.");
        grupoRepository.deleteById(grupoId);
    }

    // ---------- apoio ----------

    private Grupo buscar(Long id) {
        return grupoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Grupo não encontrado."));
    }

    private void exigirAdmin(Long grupoId, Usuario usuario, String mensagem) {
        if (!grupoMembroRepository.existsByGrupoIdAndUsuarioIdAndPapelAndBloqueadoFalse(
                grupoId, usuario.getId(), PapelGrupo.ADMIN)) {
            throw new AcessoNegadoException(mensagem);
        }
    }

    private List<GrupoResponseDTO> montarRespostas(List<Grupo> grupos) {
        if (grupos.isEmpty()) {
            return List.of();
        }
        List<Long> ids = grupos.stream().map(Grupo::getId).toList();
        Map<Long, Long> membros = paraMapa(grupoMembroRepository.contarMembrosPorGrupo(ids));
        Map<Long, Long> atividades = paraMapa(tarefaRepository.contarPorGrupo(ids));

        return grupos.stream()
                .map(g -> new GrupoResponseDTO(g,
                        membros.getOrDefault(g.getId(), 0L),
                        atividades.getOrDefault(g.getId(), 0L)))
                .toList();
    }

    private Map<Long, Long> paraMapa(List<ContagemPorGrupo> contagens) {
        return contagens.stream()
                .collect(Collectors.toMap(ContagemPorGrupo::getGrupoId, ContagemPorGrupo::getTotal, (a, b) -> a));
    }
}
