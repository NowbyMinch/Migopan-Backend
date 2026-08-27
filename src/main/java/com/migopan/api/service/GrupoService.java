package com.migopan.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.migopan.api.dto.grupo.*;
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
        return grupos.stream().map(grupo -> {
            long qtd = grupoMembroRepository.countByGrupoId(grupo.getId());
            return new GrupoResponseDTO(grupo, qtd);
        }).toList();
    }

    @Transactional
    public GrupoResponseDTO criarGrupo(GrupoRequestDTO dto, Usuario usuarioLogado) {
        Usuario usuario = usuarioRepository.findById(usuarioLogado.getId())
                .orElseThrow(() -> new NotFoundException("Usuário criador não encontrado."));

        Grupo grupo = new Grupo();
        grupo.setNome(dto.nome());
        grupo.setDescricao(dto.descricao());
        Grupo grupoSalvo = grupoRepository.save(grupo);

        GrupoMembroId id = new GrupoMembroId(grupoSalvo.getId(), usuarioLogado.getId());
        GrupoMembro membro = new GrupoMembro();
        membro.setId(id);
        membro.setGrupo(grupoSalvo);
        membro.setUsuario(usuario);
        membro.setPapel("ADMIN");
        membro.setBloqueado(false);

        grupoMembroRepository.save(membro);

        return new GrupoResponseDTO(grupoSalvo, 1L);
    }

    @Transactional
    public GrupoResponseDTO atualizarGrupo(Long grupoId, Usuario usuarioLogado, AtualizarGrupoRequestDTO dto) {
        if (!grupoMembroRepository.existsByGrupoIdAndUsuarioIdAndPapel(grupoId, usuarioLogado.getId(), "ADMIN")) {
            throw new AcessoNegadoException("Apenas administradores podem editar o grupo.");
        }

       boolean atualizarNome = (dto.nome() != null && !dto.nome().isBlank());
        boolean atualizarDescricao = (dto.descricao() != null);

        if (!atualizarNome && !atualizarDescricao) {
            throw new IllegalArgumentException("Nenhum dado foi fornecido para atualização.");
        }

        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new NotFoundException("Grupo não encontrado."));

        if (atualizarNome) { 
            grupo.setNome(dto.nome());
        }
        if (atualizarDescricao) { 
            grupo.setDescricao(dto.descricao());
        }

        Grupo salvo = grupoRepository.save(grupo);
        long qtd = grupoMembroRepository.countByGrupoId(salvo.getId());
        return new GrupoResponseDTO(salvo, qtd);
    }

    @Transactional
    public void deletarGrupo(Long grupoId, Usuario usuarioLogado) {
        if (!grupoRepository.existsById(grupoId)) {
            throw new NotFoundException("Grupo não encontrado.");
        }

        if (!grupoMembroRepository.existsByGrupoIdAndUsuarioIdAndPapel(grupoId, usuarioLogado.getId(), "ADMIN")) {
            throw new AcessoNegadoException("Apenas administradores podem deletar o grupo.");
        }

        grupoRepository.deleteById(grupoId);
    }

    public List<GrupoResponseDTO> listarTodos() {
        return grupoRepository.findAll().stream()
                .map(grupo -> { 
                    long qtd = grupoMembroRepository.countByGrupoId(grupo.getId());
                    return new GrupoResponseDTO(grupo, qtd);
                }).toList();
    }

    public GrupoResponseDTO grupoPorId(Long id) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Grupo não encontrado."));
        
        long qtd = grupoMembroRepository.countByGrupoId(id);
        return new GrupoResponseDTO(grupo, qtd);
    }
}