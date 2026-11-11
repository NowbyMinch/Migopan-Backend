package com.migopan.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.migopan.api.dto.UsuarioDTOs.AtualizarUsuarioRequestDTO;
import com.migopan.api.dto.UsuarioDTOs.PerfilResponseDTO;
import com.migopan.api.dto.UsuarioDTOs.UsuarioRequestDTO;
import com.migopan.api.dto.UsuarioDTOs.UsuarioResponseDTO;
import com.migopan.api.model.Usuario;
import com.migopan.api.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public List<PerfilResponseDTO> listar() {
        return usuarioService.listarPerfis();
    }

    @GetMapping("/perfil")
    public UsuarioResponseDTO perfilLogado(@AuthenticationPrincipal Usuario usuarioLogado) {
        return usuarioService.buscarPerfilLogado(usuarioLogado.getId());
    }

    @GetMapping("/{id}")
    public PerfilResponseDTO buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPerfilPublico(id);
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(@RequestBody @Valid UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criarUsuario(dto));
    }

    @PatchMapping("/{id}")
    public UsuarioResponseDTO atualizar(@PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestBody @Valid AtualizarUsuarioRequestDTO dto) {
        return usuarioService.atualizarUsuario(id, usuarioLogado, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuarioLogado) {
        usuarioService.deletarUsuario(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
