package com.migopan.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.migopan.api.dto.usuario.*;
import com.migopan.api.exception.NotFoundException;
import com.migopan.api.model.Usuario;
import com.migopan.api.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public List<UsuarioResponseDTO> listarTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        if (usuarios.isEmpty()) {
            throw new NotFoundException("Nenhum usuário encontrado.");
        }

        return usuarios.stream()
                .map(UsuarioResponseDTO::new)
                .toList();
    }

    public UsuarioResponseDTO buscarPerfilLogado(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
        return new UsuarioResponseDTO(usuario);
    }

    public PerfilResponseDTO buscarPerfilPublico(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
        return new PerfilResponseDTO(usuario);
    }

    // Criar usuário
    @Transactional
    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO dto){
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


    // Atualizar usuário
    // @Transactional
    // public UsuarioResponseDTO atualizarUsuario(Long id, UsuarioRequestDTO dto) {
    //     Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

    //     return new UsuarioResponseDTO(usuario);
    // }


    // Deletar usuário



}