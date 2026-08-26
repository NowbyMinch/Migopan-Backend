package com.migopan.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.migopan.api.dto.GrupoRequestDTO;
import com.migopan.api.dto.GrupoResponseDTO;
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
public class GrupoService {
    
    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private GrupoMembroRepository grupoMembroRepository;

    public List<GrupoResponseDTO> getGruposDoUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new NotFoundException("Usuário não encontrado.");
        }

        List<Grupo> grupos = grupoMembroRepository.findGruposByUsuarioId(usuarioId);
        return grupos.stream().map(GrupoResponseDTO::new).toList();
    }

    @Transactional
    public GrupoResponseDTO criarGrupo(GrupoRequestDTO dto, Long usuarioIdLogado) {
        Usuario usuario = usuarioRepository.findById(usuarioIdLogado)
                .orElseThrow(() -> new NotFoundException("Usuário criador não encontrado."));

        Grupo grupo = new Grupo();
        grupo.setNome(dto.nome());
        grupo.setDescricao(dto.descricao());
        Grupo grupoSalvo = grupoRepository.save(grupo);

        GrupoMembroId id = new GrupoMembroId(grupoSalvo.getId(), usuarioIdLogado);
        GrupoMembro membro = new GrupoMembro();
        membro.setId(id);
        membro.setGrupo(grupoSalvo);
        membro.setUsuario(usuario);
        membro.setPapel("ADMIN");
        membro.setBloqueado(false);

        grupoMembroRepository.save(membro);

        return new GrupoResponseDTO(grupoSalvo);
    }

    @Transactional
    public GrupoResponseDTO atualizarGrupo(Long grupoId, Long usuarioIdLogado, GrupoRequestDTO dto) {
        if (!grupoMembroRepository.existsByGrupoIdAndUsuarioIdAndPapel(grupoId, usuarioIdLogado, "ADMIN")) {
            throw new RuntimeException("Apenas administradores podem editar o grupo.");
        }

        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new NotFoundException("Grupo não encontrado."));

        if (dto.nome() != null && !dto.nome().isBlank()) {
            grupo.setNome(dto.nome());
        }
        if (dto.descricao() != null) {
            grupo.setDescricao(dto.descricao());
        }

        Grupo salvo = grupoRepository.save(grupo);
        return new GrupoResponseDTO(salvo);
    }

    @Transactional
    public void deletarGrupo(Long grupoId, Long usuarioIdLogado) {
        if (!grupoMembroRepository.existsByGrupoIdAndUsuarioIdAndPapel(grupoId, usuarioIdLogado, "ADMIN")) {
            throw new RuntimeException("Apenas administradores podem deletar o grupo.");
        }
    
        if (!grupoRepository.existsById(grupoId)) {
            throw new NotFoundException("Grupo não encontrado.");
        }

        grupoRepository.deleteById(grupoId);
    }

    public List<GrupoResponseDTO> listarTodos() {
        return grupoRepository.findAll().stream()
                .map(grupo -> { 
                    long qtd = grupoMembroRepository.countByGroupId(grupo.getId());
                    return new GrupoResponseDTO(grupo, qtd);
                }).toList();
    }

    public GrupoResponseDTO grupoPorId(Long id) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Grupo não encontrado."));
        
        long qtd = grupoMembroRepository.countByGroupId(id);
        return new GrupoResponseDTO(grupo, qtd);
    }
}