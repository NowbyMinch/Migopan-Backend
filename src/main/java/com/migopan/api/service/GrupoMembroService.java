package com.migopan.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.migopan.api.dto.GrupoMembroDTOs.*;
import com.migopan.api.exception.AcessoNegadoException;
import com.migopan.api.exception.NotFoundException;
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

    public List<GrupoMembroResponseDTO> getMembrosPorGrupo(Long grupoId) {
        if (!grupoRepository.existsById(grupoId)) {
            throw new NotFoundException("Grupo não encontrado.");
        }
        
        List<GrupoMembro> membros = grupoMembroRepository.findByGrupoIdWithDetails(grupoId);
        
        return membros.stream()
                .map(GrupoMembroResponseDTO::new)
                .toList();
    }

    public boolean verificarSeAdmin(Long grupoId, Long usuarioId) {
        return grupoMembroRepository.existsByGrupoIdAndUsuarioIdAndPapel(grupoId, usuarioId, "ADMIN");
    }

    @Transactional
    public GrupoMembroResponseDTO adicionarMembro(Long grupoId, Usuario usuarioLogado, GrupoMembroRequestDTO dto){
        if (!verificarSeAdmin(grupoId, usuarioLogado.getId())) {
            throw new AcessoNegadoException("Apenas administradores podem adicionar membros.");
        }

        Grupo grupo = grupoRepository.findById(grupoId)
            .orElseThrow(() -> new NotFoundException("Grupo não encontrado."));

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
            .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

        GrupoMembroId id = new GrupoMembroId(grupoId, dto.usuarioId());        
        if (grupoMembroRepository.existsById(id)) {
            throw new IllegalArgumentException("Este usuário já é membro do grupo.");
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

        return new GrupoMembroResponseDTO(salvo);
    }

    @Transactional
    public void removerMembro(Long grupoId, Long usuarioIdAdminLogado, Long usuarioIdMembro) {
        if (!verificarSeAdmin(grupoId, usuarioIdAdminLogado)) {
            throw new AcessoNegadoException("Apenas administradores podem remover membros.");
        }

        GrupoMembroId id = new GrupoMembroId(grupoId, usuarioIdMembro);
        if (!grupoMembroRepository.existsById(id)) {
            throw new NotFoundException("Usuário não é membro do grupo.");
        }

        grupoMembroRepository.deleteById(id);
    }

    @Transactional
    public GrupoMembroResponseDTO atualizarMembro(Long grupoId, Long usuarioIdAdminLogado, Long usuarioIdMembro, AtualizarMembroRequestDTO dto) {
        if (!verificarSeAdmin(grupoId, usuarioIdAdminLogado)) {
            throw new AcessoNegadoException("Apenas administradores podem atualizar membros.");
        }

        GrupoMembroId id = new GrupoMembroId(grupoId, usuarioIdMembro);
        GrupoMembro grupoMembro = grupoMembroRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não é membro do grupo."));

        if (dto.bloqueado() != null) {
            grupoMembro.setBloqueado(dto.bloqueado());
        }

        if (dto.papel() != null && !dto.papel().isBlank()) {
            grupoMembro.setPapel(dto.papel());
        }

        GrupoMembro salvo = grupoMembroRepository.save(grupoMembro);
        return new GrupoMembroResponseDTO(salvo);
    }

}