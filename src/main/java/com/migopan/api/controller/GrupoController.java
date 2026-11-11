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

import com.migopan.api.dto.GrupoDTOs.AtualizarGrupoRequestDTO;
import com.migopan.api.dto.GrupoDTOs.GrupoRequestDTO;
import com.migopan.api.dto.GrupoDTOs.GrupoResponseDTO;
import com.migopan.api.model.Usuario;
import com.migopan.api.service.GrupoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/grupos")
@RequiredArgsConstructor
public class GrupoController {

    private final GrupoService grupoService;

    @GetMapping
    public List<GrupoResponseDTO> listarMeus(@AuthenticationPrincipal Usuario usuarioLogado) {
        return grupoService.listarDoUsuario(usuarioLogado.getId());
    }

    @GetMapping("/{id}")
    public GrupoResponseDTO buscar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuarioLogado) {
        return grupoService.buscarPorId(id, usuarioLogado);
    }

    @PostMapping
    public ResponseEntity<GrupoResponseDTO> criar(@RequestBody @Valid GrupoRequestDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.status(HttpStatus.CREATED).body(grupoService.criarGrupo(dto, usuarioLogado));
    }

    @PatchMapping("/{id}")
    public GrupoResponseDTO atualizar(@PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestBody @Valid AtualizarGrupoRequestDTO dto) {
        return grupoService.atualizarGrupo(id, usuarioLogado, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuarioLogado) {
        grupoService.deletarGrupo(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
