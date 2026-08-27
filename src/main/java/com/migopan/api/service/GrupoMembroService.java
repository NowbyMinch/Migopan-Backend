package com.migopan.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import com.migopan.api.dto.membro.*;
import com.migopan.api.model.Grupo;
import com.migopan.api.model.GrupoMembro;
import com.migopan.api.model.Usuario;
import com.migopan.api.model.keys.GrupoMembroId;
import com.migopan.api.repository.GrupoMembroRepository;
import com.migopan.api.repository.GrupoRepository;
import com.migopan.api.repository.UsuarioRepository;


import jakarta.transaction.Transactional;

@Service
public class GrupoMembroService {
    @Autowired
    private GrupoMembroRepository grupoMembroRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<GrupoMembroResponseDTO> getMembrosPorGrupo(Long grupoId){
        List<GrupoMembro> membros = grupoMembroRepository.findByGrupoIdWithDetails(grupoId);
        
        long qtd = grupoMembroRepository.countByGrupoId(grupoId);

        return membros.stream()
                .map(membro -> new GrupoMembroResponseDTO(membro, qtd))
                .toList();
    }

    public boolean verificarSeAdmin(Long grupoId, Long usuarioId) {
        return grupoMembroRepository.existsByGrupoIdAndUsuarioIdAndPapel(grupoId, usuarioId, "ADMIN");
    }

    @Transactional
    public GrupoMembroResponseDTO adicionarMembro(Long grupoId, Usuario usuarioLogado, GrupoMembroRequestDTO dto){
        if (!verificarSeAdmin(grupoId, usuarioLogado.getId())) {
            throw new RuntimeException("Apenas administradores podem adicionar membros.");
        }

        Grupo grupo = grupoRepository.findById(grupoId)
            .orElseThrow(() -> new RuntimeException("Grupo não encontrado."));

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        GrupoMembroId id = new GrupoMembroId(grupoId, dto.usuarioId());        
        if (grupoMembroRepository.existsById(id)) {
            throw new RuntimeException("Este usuário já é membro do grupo.");
        }

        GrupoMembro grupoMembro = new GrupoMembro();
        grupoMembro.setId(id);
        grupoMembro.setGrupo(grupo);
        grupoMembro.setUsuario(usuario);

        if (dto.papel() != null && !dto.papel().isBlank()) {
            grupoMembro.setPapel(dto.papel());
        } else {
             grupoMembro.setPapel("MEMBRO");
        }

        grupoMembro.setBloqueado(false);

        GrupoMembro salvo = grupoMembroRepository.save(grupoMembro);

        long qtd = grupoMembroRepository.countByGrupoId(grupoId);
        return new GrupoMembroResponseDTO(salvo, qtd);
    }

    @Transactional
    public String removerMembro(Long grupoId, Long usuarioIdAdminLogado, Long usuarioIdMembro) {
        if (!verificarSeAdmin(grupoId, usuarioIdAdminLogado)) {
            throw new RuntimeException("Apenas administradores podem remover membros.");
        }

        GrupoMembroId id = new GrupoMembroId(grupoId, usuarioIdMembro);
        if (!grupoMembroRepository.existsById(id)) {
            throw new RuntimeException("Usuário não é membro do grupo.");
        }

        grupoMembroRepository.deleteById(id);
        return "Membro removido com sucesso.";
    }

    @Transactional
    public GrupoMembroResponseDTO atualizarMembro(Long grupoId, Long usuarioIdAdminLogado, Long usuarioIdMembro, AtualizarMembroRequestDTO dto) {
        if (!verificarSeAdmin(grupoId, usuarioIdAdminLogado)) {
            throw new RuntimeException("Apenas administradores podem atualizar membros.");
        }

        GrupoMembroId id = new GrupoMembroId(grupoId, usuarioIdMembro);
        GrupoMembro grupoMembro = grupoMembroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não é membro do grupo."));

        // Atualiza o status de bloqueio se foi enviado
        if (dto.bloqueado() != null) {
            grupoMembro.setBloqueado(dto.bloqueado());
        }

        // Se você quiser permitir atualizar o papel também de forma controlada, pode adicionar aqui:
        if (dto.papel() != null && !dto.papel().isBlank()) {
            grupoMembro.setPapel(dto.papel());
        }

        GrupoMembro salvo = grupoMembroRepository.save(grupoMembro);
        return new GrupoMembroResponseDTO(salvo);
    }

}