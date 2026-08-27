package com.migopan.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.migopan.api.dto.grupo.*;
import com.migopan.api.model.Usuario;
import com.migopan.api.service.GrupoService;


import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/grupos")
@CrossOrigin(origins = "*")
public class GrupoController {

    @Autowired
    private GrupoService grupoService;

    @GetMapping
    public ResponseEntity<?> getAllGrupos() {
        List<GrupoResponseDTO> grupos = grupoService.listarTodos();
        
        if (grupos.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "Nenhum grupo encontrado."));
        }

        return ResponseEntity.ok(grupos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoResponseDTO> getGrupo(@PathVariable Long id) {
        GrupoResponseDTO grupo = grupoService.grupoPorId(id);
        
        return ResponseEntity.ok(grupo);
    }

    @PostMapping
    public ResponseEntity<GrupoResponseDTO> create(@RequestBody @Valid GrupoRequestDTO dto, @AuthenticationPrincipal Usuario usuarioLogado) {
        GrupoResponseDTO saved = grupoService.criarGrupo(dto, usuarioLogado);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @AuthenticationPrincipal Usuario usuarioLogado) {
        grupoService.deletarGrupo(id, usuarioLogado);
        return ResponseEntity.noContent().build(); 
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @AuthenticationPrincipal Usuario usuarioLogado, @RequestBody @Valid AtualizarGrupoRequestDTO dto) {
        GrupoResponseDTO atualizado = grupoService.atualizarGrupo(id, usuarioLogado, dto);
        return ResponseEntity.ok(atualizado);
    }

}
