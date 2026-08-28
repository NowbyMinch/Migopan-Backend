package com.migopan.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.migopan.api.dto.UsuarioDTOs.*;
import com.migopan.api.exception.AcessoNegadoException;
import com.migopan.api.exception.NotFoundException;
import com.migopan.api.model.Usuario;
import com.migopan.api.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        if (usuarios.isEmpty()) {
            throw new NotFoundException("Nenhum usuário encontrado.");
        }

        return usuarios.stream()
                .map(UsuarioResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
        return new UsuarioResponseDTO(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPerfilLogado(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
        return new UsuarioResponseDTO(usuario);
    }

    @Transactional(readOnly = true)
    public PerfilResponseDTO buscarPerfilPublico(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
        return new PerfilResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenhaHash(passwordEncoder.encode(dto.senha()));
        usuario.setEmailVerificado(false);
        usuario.setAtivo(true);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return new UsuarioResponseDTO(usuarioSalvo);
    }

    @Transactional
    public UsuarioResponseDTO atualizarUsuario(Long id, Usuario usuarioLogado, AtualizarUsuarioRequestDTO dto) {
        if (!id.equals(usuarioLogado.getId())) {
            throw new AcessoNegadoException("Você não tem permissão para alterar este perfil.");
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

        if (dto.email() != null && !dto.email().isBlank() && !dto.email().equalsIgnoreCase(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(dto.email())) {
                throw new IllegalArgumentException("Email já está em uso por outra conta.");
            }
            usuario.setEmail(dto.email());
        }

        if (dto.nome() != null && !dto.nome().isBlank()) {
            usuario.setNome(dto.nome());
        }

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return new UsuarioResponseDTO(usuarioSalvo);
    }

    @Transactional
    public void deletarUsuario(Long id, Usuario usuarioLogado) {
        if (!id.equals(usuarioLogado.getId())) {
            throw new AcessoNegadoException("Você não tem permissão para deletar esta conta.");
        }

        if (!usuarioRepository.existsById(id)) {
            throw new NotFoundException("Usuário não encontrado.");
        }

        usuarioRepository.deleteById(id);
    }
}