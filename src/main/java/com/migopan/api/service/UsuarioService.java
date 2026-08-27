package com.migopan.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.migopan.api.dto.UsuarioRequestDTO;
import com.migopan.api.dto.UsuarioResponseDTO;
import com.migopan.api.dto.PerfilResponseDTO;
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

    public buscarPerfilLogado(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> throw new NotFoundException("Usuário não encontrado."));
        return new UsuarioResponseDTO(usuario);
    }

    public buscarPerfilPublico(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> throw new NotFoundException("Usuário não encontrado."));
        return new PerfilResponseDTO(usuario);
    }

    // Criar usuário
    @Transactional
    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO dto){
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setEmailVerificado(false);
        novoUsuario.setAtivo(true);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return new UsuarioResponseDTO(usuarioSalvo);
    }


    // Atualizar usuário
    @Transactional
    public UsuarioResponseDTO atualizarUsuario(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id);


        return new UsuarioResponseDTO(usuarioSalvo);
    }


    // Deletar usuário



}