package com.migopan.api.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.migopan.api.model.Usuario;
import com.migopan.api.repository.UsuarioRepository;
import com.migopan.api.dto.UsuarioRequestDTO;
import com.migopan.api.dto.UsuarioResponseDTO;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {
    @Autowired
    private UsuarioRepository UsuarioRepository;

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Usuario> usuarios = UsuarioRepository.findAll();
        
        if (usuarios.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Nenhum usuário encontrado"));
        }

        List<UsuarioResponseDTO> usuariosResponse = usuarios.stream()
            .map(UsuarioResponseDTO::new)
            .toList();

        return ResponseEntity.ok(usuariosResponse);
    };

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        Optional<Usuario> usuario = UsuarioRepository.findById(id);
        
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Nenhum usuário encontrado"));
        }

        UsuarioResponseDTO usuarioResponse = new UsuarioResponseDTO(usuario.get());
        return ResponseEntity.ok(usuarioResponse);
    };
    
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid UsuarioRequestDTO dto) {
        
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dto.nome());
        novoUsuario.setEmail(dto.email());
        novoUsuario.setSenhaHash(dto.senha());
        novoUsuario.setEmailVerificado(false);
        
        UsuarioResponseDTO usuarioSalvo = new UsuarioResponseDTO(UsuarioRepository.save(novoUsuario));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Usuário criado com sucesso: " + usuarioSalvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid UsuarioRequestDTO dto) {
        Optional<Usuario> usuario = UsuarioRepository.findById(id);

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Usuário não encontrado"));
        }
        
        Usuario usuarioAtualizado = usuario.get();
        usuarioAtualizado.setNome(dto.nome());
        usuarioAtualizado.setEmail(dto.email());
        usuarioAtualizado.setSenhaHash(dto.senha());

        UsuarioRepository.save(usuarioAtualizado);
        return ResponseEntity.ok("Usuário atualizado com sucesso" + usuarioAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<Usuario> usuario = UsuarioRepository.findById(id);
        
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Nenhum usuário encontrado"));
        }

        UsuarioRepository.deleteById(id);

        return ResponseEntity.ok(Map.of("message", "usuário deletado com sucesso"));
    };
    
}