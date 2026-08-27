package com.migopan.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<GrupoMembroResponseDTO> getMembrosPorGrupo(Long grupoId){
        return grupoMembroRepository.findByGrupoId(grupoId).stream()
                .map(GrupoMembroResponseDTO::new)
                .toList();
    }

    public boolean verificarSeAdmin(Long grupoId, Long usuarioId) {
        boolean grupoMembroRepository.existsByGroupIdAndUsuarioIdAndPapel(grupoId, usuarioId, "ADMIN");
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
        return new GrupoMembroResponseDTO(salvo);
    }

    @Transactional
    public String removerMembro(Long grupoId, Long usuarioIdAdminLogado, Long usuarioIdMembro) {`
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
    public String atualizarPapel(Long grupoId, Long usuarioIdAdminLogado, Long usuarioIdMembro, String novoPapel) {
        if (!verificarSeAdmin(grupoId, usuarioIdAdminLogado)) {
            throw new RuntimeException("Apenas administradores podem atualizar o papel dos membros.");
        }

        GrupoMembro id = new GrupoMembroId(grupoId, usuarioIdMembro);
        
        GrupoMembro grupoMembro = grupoMembroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não é membro do grupo."));
        
        GrupoMembro membro = grupoMembro.get();
        membro.setPapel(novoPapel);
        grupoMembroRepository.save(membro);

        return "Papel do membro atualizado com sucesso.";
    }

}