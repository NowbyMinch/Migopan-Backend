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

import com.migopan.api.dto.GrupoMembroDTOs.AtualizarMembroRequestDTO;
import com.migopan.api.dto.GrupoMembroDTOs.GrupoMembroRequestDTO;
import com.migopan.api.dto.GrupoMembroDTOs.GrupoMembroResponseDTO;
import com.migopan.api.model.Usuario;
import com.migopan.api.service.GrupoMembroService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/grupos/{grupoId}/membros")
@RequiredArgsConstructor
public class GrupoMembroController {

    private final GrupoMembroService grupoMembroService;

    @GetMapping
    public List<GrupoMembroResponseDTO> listar(@PathVariable Long grupoId,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        return grupoMembroService.listarMembros(grupoId, usuarioLogado);
    }

    @PostMapping
    public ResponseEntity<GrupoMembroResponseDTO> adicionar(@PathVariable Long grupoId,
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestBody @Valid GrupoMembroRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(grupoMembroService.adicionarMembro(grupoId, usuarioLogado, dto));
    }

    @PatchMapping("/{usuarioIdMembro}")
    public GrupoMembroResponseDTO atualizar(@PathVariable Long grupoId,
            @PathVariable Long usuarioIdMembro,
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestBody @Valid AtualizarMembroRequestDTO dto) {
        return grupoMembroService.atualizarMembro(grupoId, usuarioLogado, usuarioIdMembro, dto);
    }

    @DeleteMapping("/{usuarioIdMembro}")
    public ResponseEntity<Void> remover(@PathVariable Long grupoId,
            @PathVariable Long usuarioIdMembro,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        grupoMembroService.removerMembro(grupoId, usuarioLogado, usuarioIdMembro);
        return ResponseEntity.noContent().build();
    }
}
