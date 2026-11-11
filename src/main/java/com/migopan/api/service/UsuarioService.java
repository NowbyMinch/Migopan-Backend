package com.migopan.api.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.migopan.api.dto.UsuarioDTOs.AtualizarUsuarioRequestDTO;
import com.migopan.api.dto.UsuarioDTOs.PerfilResponseDTO;
import com.migopan.api.dto.UsuarioDTOs.UsuarioRequestDTO;
import com.migopan.api.dto.UsuarioDTOs.UsuarioResponseDTO;
import com.migopan.api.exception.AcessoNegadoException;
import com.migopan.api.exception.NotFoundException;
import com.migopan.api.model.Usuario;
import com.migopan.api.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<PerfilResponseDTO> listarPerfis() {
        return usuarioRepository.findAll().stream().map(PerfilResponseDTO::new).toList();
    }

    public PerfilResponseDTO buscarPerfilPublico(Long id) {
        return new PerfilResponseDTO(buscar(id));
    }

    public UsuarioResponseDTO buscarPerfilLogado(Long usuarioId) {
        return new UsuarioResponseDTO(buscar(usuarioId));
    }

    @Transactional
    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO dto) {
        String email = dto.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome().trim());
        usuario.setEmail(email);
        usuario.setSenhaHash(passwordEncoder.encode(dto.senha()));
        usuario.setEmailVerificado(false);
        usuario.setAtivo(true);

        return new UsuarioResponseDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO atualizarUsuario(Long id, Usuario usuarioLogado, AtualizarUsuarioRequestDTO dto) {
        garantirDono(id, usuarioLogado, "Você não tem permissão para alterar este perfil.");
        Usuario usuario = buscar(id);

        if (dto.email() != null && !dto.email().isBlank()) {
            String novoEmail = dto.email().trim().toLowerCase();
            if (!novoEmail.equals(usuario.getEmail())) {
                if (usuarioRepository.existsByEmail(novoEmail)) {
                    throw new IllegalArgumentException("Email já está em uso por outra conta.");
                }
                usuario.setEmail(novoEmail);
            }
        }
        if (dto.nome() != null && !dto.nome().isBlank()) {
            usuario.setNome(dto.nome().trim());
        }

        return new UsuarioResponseDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public void deletarUsuario(Long id, Usuario usuarioLogado) {
        garantirDono(id, usuarioLogado, "Você não tem permissão para deletar esta conta.");
        if (!usuarioRepository.existsById(id)) {
            throw new NotFoundException("Usuário não encontrado.");
        }
        usuarioRepository.deleteById(id);
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
    }

    private void garantirDono(Long id, Usuario logado, String mensagem) {
        if (!id.equals(logado.getId())) {
            throw new AcessoNegadoException(mensagem);
        }
    }
}
