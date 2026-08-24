package com.migopan.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.migopan.api.dto.GrupoRequestDTO;
import com.migopan.api.model.Grupo;
import com.migopan.api.model.GrupoMembro;
import com.migopan.api.model.Usuario;
import com.migopan.api.repository.GrupoMembroRepository;
import com.migopan.api.repository.GrupoRepository;

import jakarta.transaction.Transactional;

@Service
public class GrupoService {
    
    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private GrupoMembroRepository grupoMembroRepository;

    public List<GrupoResponseDTO> listarTodos() {
        return grupoRepository.findAll().stream()
                .map(grupo -> { 
                    long qtd = grupoMembroRepository.countByGroupId(grupo.getId());
                    return new GrupoResponseDTO(grupo, qtd);
                }).toList();
    }

    public Optional<GrupoResponseDTO> GrupoPorId(Long id) {
        Optional<Grupo> grupo = grupoRepository.findById(id).map(grupo -> {
            long qtd = grupoMembroRepository.countByGroupId(id);
            return new GrupoResponseDTO(grupo, qtd);
        });

        return grupo;
    }

    @Transactional
    public Grupo criarGrupo(GrupoRequestDTO dto, Usuario criador) {
        if (criador != null) {
            Grupo grupo = new Grupo();
            grupo.setNome(dto.nome());
            grupo.setDescricao(dto.descricao());
            Grupo grupoSalvo = grupoRepository.save(grupo);

            GrupoMembro membro = new GrupoMembro();
            membro.setUsuario(criador);
            membro.setGrupo(grupoSalvo);
            membro.setPapel("ADMIN");
            membro.setBloqueado(false);
            grupoMembroRepository.save(membro);

            return grupoSalvo;
        }

        throw new RuntimeException("Usuário não encontrado.");
    }
    
    public boolean deletar(Long id) {
        if (!grupoRepository.existsById(id)) {
            return false;
        }

        grupoRepository.deleteById(id);
        return true;
    }

    @Transactional
    public Optional<Grupo> atualizar(Long id, GrupoRequestDTO dto) {
        return grupoRepository.findById(id).map(grupo -> {
            grupo.setNome(dto.nome());
            grupo.setDescricao(dto.descricao());
            return grupoRepository.save(grupo);
        });
    }
}
