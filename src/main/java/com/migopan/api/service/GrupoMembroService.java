package com.migopan.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.migopan.api.dto.GrupoMembroDTOs.AtualizarMembroRequestDTO;
import com.migopan.api.dto.GrupoMembroDTOs.GrupoMembroRequestDTO;
import com.migopan.api.dto.GrupoMembroDTOs.GrupoMembroResponseDTO;
import com.migopan.api.exception.AcessoNegadoException;
import com.migopan.api.exception.NotFoundException;
import com.migopan.api.model.Grupo;
import com.migopan.api.model.GrupoMembro;
import com.migopan.api.model.Usuario;
import com.migopan.api.model.enums.PapelGrupo;
import com.migopan.api.model.keys.GrupoMembroId;
import com.migopan.api.repository.GrupoMembroRepository;
import com.migopan.api.repository.GrupoRepository;
import com.migopan.api.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GrupoMembroService {

    private final GrupoMembroRepository grupoMembroRepository;
    private final GrupoRepository grupoRepository;
    private final UsuarioRepository usuarioRepository;

    public List<GrupoMembroResponseDTO> listarMembros(Long grupoId, Usuario usuarioLogado) {
        if (!grupoRepository.existsById(grupoId)) {
            throw new NotFoundException("Grupo não encontrado.");
        }
        if (!grupoMembroRepository.existsByGrupoIdAndUsuarioIdAndBloqueadoFalse(grupoId, usuarioLogado.getId())) {
            throw new AcessoNegadoException("Você não tem acesso aos membros deste grupo.");
        }
        return grupoMembroRepository.findByGrupoIdWithDetails(grupoId).stream()
                .map(GrupoMembroResponseDTO::new)
                .toList();
    }

    @Transactional
    public GrupoMembroResponseDTO adicionarMembro(Long grupoId, Usuario usuarioLogado, GrupoMembroRequestDTO dto) {
        exigirAdmin(grupoId, usuarioLogado, "Apenas administradores podem adicionar membros.");

        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new NotFoundException("Grupo não encontrado."));
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

        GrupoMembroId id = new GrupoMembroId(grupoId, dto.usuarioId());
        if (grupoMembroRepository.existsById(id)) {
            throw new IllegalArgumentException("Este usuário já é membro do grupo.");
        }

        GrupoMembro membro = new GrupoMembro();
        membro.setId(id);
        membro.setGrupo(grupo);
        membro.setUsuario(usuario);
        membro.setPapel(dto.papel() != null ? dto.papel() : PapelGrupo.MEMBRO);
        membro.setBloqueado(false);

        return new GrupoMembroResponseDTO(grupoMembroRepository.save(membro));
    }

    @Transactional
    public GrupoMembroResponseDTO atualizarMembro(Long grupoId, Usuario usuarioLogado, Long usuarioIdMembro,
            AtualizarMembroRequestDTO dto) {
        exigirAdmin(grupoId, usuarioLogado, "Apenas administradores podem atualizar membros.");
        GrupoMembro membro = buscarMembro(grupoId, usuarioIdMembro);

        boolean deixaDeSerAdminAtivo = (dto.papel() != null && dto.papel() != PapelGrupo.ADMIN)
                || Boolean.TRUE.equals(dto.bloqueado());
        if (deixaDeSerAdminAtivo) {
            garantirQueNaoSeraOUltimoAdmin(grupoId, membro);
        }

        if (dto.papel() != null) {
            membro.setPapel(dto.papel());
        }
        if (dto.bloqueado() != null) {
            membro.setBloqueado(dto.bloqueado());
        }

        return new GrupoMembroResponseDTO(grupoMembroRepository.save(membro));
    }

    @Transactional
    public void removerMembro(Long grupoId, Usuario usuarioLogado, Long usuarioIdMembro) {
        exigirAdmin(grupoId, usuarioLogado, "Apenas administradores podem remover membros.");
        GrupoMembro membro = buscarMembro(grupoId, usuarioIdMembro);
        garantirQueNaoSeraOUltimoAdmin(grupoId, membro);
        grupoMembroRepository.delete(membro);
    }

    // ---------- apoio ----------

    private GrupoMembro buscarMembro(Long grupoId, Long usuarioId) {
        return grupoMembroRepository.findById(new GrupoMembroId(grupoId, usuarioId))
                .orElseThrow(() -> new NotFoundException("Usuário não é membro do grupo."));
    }

    private void exigirAdmin(Long grupoId, Usuario usuario, String mensagem) {
        if (!grupoMembroRepository.existsByGrupoIdAndUsuarioIdAndPapelAndBloqueadoFalse(
                grupoId, usuario.getId(), PapelGrupo.ADMIN)) {
            throw new AcessoNegadoException(mensagem);
        }
    }

    private void garantirQueNaoSeraOUltimoAdmin(Long grupoId, GrupoMembro alvo) {
        boolean alvoEhAdminAtivo = alvo.getPapel() == PapelGrupo.ADMIN && !Boolean.TRUE.equals(alvo.getBloqueado());
        if (alvoEhAdminAtivo
                && grupoMembroRepository.countByGrupoIdAndPapelAndBloqueadoFalse(grupoId, PapelGrupo.ADMIN) <= 1) {
            throw new IllegalArgumentException("O grupo precisa ter pelo menos um administrador ativo.");
        }
    }
}
