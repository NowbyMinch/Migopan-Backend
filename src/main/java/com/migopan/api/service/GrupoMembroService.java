package com.migopan.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.migopan.api.exception.NotFoundException;
import com.migopan.api.dto.AtualizarMembroRequestDTO;
import com.migopan.api.dto.GrupoMembroRequestDTO;
import com.migopan.api.dto.GrupoMembroResponseDTO;
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

    public boolean verificarSeAdmin(Long grupoId, Long usuarioId) {
        return grupoMembroRepository.existsByGrupoIdAndUsuarioIdAndPapel(grupoId, usuarioId, "ADMIN");
    }

    public List<GrupoMembroResponseDTO> getMembrosPorGrupo(Long grupoId){
        if (!grupoRepository.existsById(grupoId)){
            throw new NotFoundException("Grupo não encontrado.");
        }

        long totalMembros = grupoMembroRepository.countByGroupId(grupoId);
        List<GrupoMembro> membros = grupoMembroRepository.findByGrupoIdWithDetails(grupoId);
        
        return membros.stream()
                .map(membro -> new GrupoMembroResponseDTO(membro, totalMembro))
                .toList();  
    }

    @Transactional
    public GrupoMembroResponseDTO adicionarMembro(Long grupoId, Long usuarioIdAdminLogado, GrupoMembroRequestDTO dto){
        if (!verificarSeAdmin(grupoId, usuarioIdAdminLogado)) {
            throw new RuntimeException("Apenas administradores podem adicionar membros.");
        }

        Grupo grupo = grupoRepository.findById(grupoId)
            .orElseThrow(() -> new RuntimeException("Grupo não encontrado."));

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        GrupoMembroId id = new GrupoMembroId(grupoId, dto.usuarioId());        
        if (grupoMembroRepository.existsById(id)) {
            throw new RuntimeException("Usuário já é membro do grupo.");
        }

        GrupoMembro grupoMembro = new GrupoMembro();
        grupoMembro.setId(id);
        grupoMembro.setGrupo(grupo);
        grupoMembro.setUsuario(usuario);

        if (dto.papel() != null && !dto.papel().isBlank()) {
            grupoMembro.setPapel(dto.papel());
        } 

        grupoMembro.setBloqueado(false);

        GrupoMembro salvo = grupoMembroRepository.save(grupoMembro);
        long totalMembros = grupoMembroRepository.countByGroupId(grupoId);

        return new GrupoMembroResponseDTO(salvo, totalMembros);
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
    public String atualizarMembro(Long grupoId, Long usuarioIdAdminLogado, Long usuarioIdMembro, AtualizarMembroRequestDTO dto) {
        if (!verificarSeAdmin(grupoId, usuarioIdAdminLogado)) {
            throw new RuntimeException("Apenas administradores podem atualizar o papel dos membros.");
        }

        boolean papelVazio = (dto.papel() == null || dto.papel().isBlank());
        boolean bloqueadoVazio = (dto.bloqueado() == null);

        if (papelVazio && bloqueadoVazio) {
            throw new IllegalArgumentException("Nenhum dado foi fornecido para atualização.");
        }

        GrupoMembroId id = new GrupoMembroId(grupoId, usuarioIdMembro);

        GrupoMembro membro = grupoMembroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não é membro do grupo."));
        
        if (!papelVazio) {
            membro.setPapel(dto.papel());
        }
        if (!bloqueadoVazio) {
            membro.setBloqueado(dto.bloqueado());
        }

        grupoMembroRepository.save(membro);

        return "Membro atualizado com sucesso.";
    }

}