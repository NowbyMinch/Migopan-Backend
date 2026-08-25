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
    private UsuarioRepository usuarioRepository;

    @Autowired
    private GrupoMembroRepository grupoMembroRepository;

    public List<GrupoResponseDTO> getGruposDoUsuario(Long usuarioId){
        if (!usuarioRepository.existsById(usuarioId)){
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

        GrupoMembro id = new GrupoMembroId(grupoSalvo.getId(), usuarioIdLogado);
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
    public GrupoResponseDTO atualizarGrupo(Long grupoId, Long usuarioIdLogado, GrupoRequestDTO dto){
        if (!grupoMembroRepository.existsByGrupoIdAndUsuarioIdAndPapel(grupoId, usuarioIdLogado, "ADMIN")){
            throw new RuntimeException("Apenas administradores podem editar o grupo.");
        }

        Grupo grupo = grupoRepository.findById(grupoId).orElseThrow(() -> new NotFoundException("Grupo não encontrado"));

        if (dto.nome() != null && !dto.nome.isBlank()) {
            grupo.setNome(dto.nome());
        }
        if (dto.descricao() != null) {
            grupo.setDescricao(dto.descricao());
        }
    
        Grupo salvo = grupoRepository.save(grupo);

        return new GrupoResponseDTO(salvo);
    }

    @Transactional
    public String deletarGrupo(Long grupoId, Long usuarioIdLogado) {
        if (!grupoMembroRepository.existsByGrupoIdAndUsuarioIdAndPapel(grupoId, usuarioIdLogado, "ADMIN")){
            throw new RuntimeException("Apenas administradores podem deletar o grupo.");
        }
    
        if (!grupoRepository.existsById(grupoId)){
            throw new NotFoundException("Grupo não encontrado.");
        }

        grupoRepository.deleteById(grupoId);

        return "Grupo deletado com sucesso."
    }

    public List<GrupoResponseDTO> listarTodos() {
        return grupoRepository.findAll().stream()
                .map(grupo -> { 
                    long qtd = grupoMembroRepository.countByGroupId(grupo.getId());
                    return new GrupoResponseDTO(grupo, qtd);
                }).toList();
    }

    public Optional<GrupoResponseDTO> GrupoPorId(Long id) {
        return grupoRepository.findById(id).map(grupo -> {
            long qtd = grupoMembroRepository.countByGroupId(id);
            return new GrupoResponseDTO(grupo, qtd)
        })
    }
    
    public boolean deletar(Long id) {
        if (!grupoRepository.existsById(id)) {
            return false;
        }

        grupoRepository.deleteById(id);
        return true;
    }

}
