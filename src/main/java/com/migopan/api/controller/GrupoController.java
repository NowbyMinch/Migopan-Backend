package com.migopan.api.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.migopan.api.dto.GrupoRequestDTO;
import com.migopan.api.dto.GrupoResponseDTO;
import com.migopan.api.model.Grupo;
import com.migopan.api.model.Usuario;
import com.migopan.api.repository.GrupoRepository;
import com.migopan.api.service.GrupoService;


import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/grupos")
@CrossOrigin(origins = "*")
public class GrupoController {

    @Autowired
    private GrupoService grupoService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<GrupoResponseDTO> grupos = grupoService.listarTodos();
        
        if (grupos.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "Nenhum grupo encontrado."));
        }

        return ResponseEntity.ok(grupos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGrupo(@PathVariable Long id) {
        Optional<GrupoResponseDTO> grupo = grupoService.GrupoPorId(id);
        if (grupo.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "Grupo não encontrado."));
        }
        
        return ResponseEntity.ok(new GrupoResponseDTO(grupo.get()));
    }

    @PostMapping
    public ResponseEntity<GrupoResponseDTO> create(@RequestBody @Valid GrupoRequestDTO dto, @AuthenticationPrincipal Usuario usuarioLogado) {
        Grupo saved = grupoService.criarGrupo(dto, usuarioLogado);

        return ResponseEntity.status(HttpStatus.CREATED).body(new GrupoResponseDTO(saved, 1L));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean deletado = grupoService.deletar(id);

        if (!deletado) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Grupo não encontrado com o ID: " + id));
        }

        return ResponseEntity.ok(Map.of("message", "Grupo com ID: " + id + " deletado com sucesso."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid GrupoRequestDTO dto) {
        Optional<Grupo> saved = grupoService.atualizar(id, dto);

        if (saved.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Grupo não encontrado com o ID: " + id));
        }

        return ResponseEntity.ok(Map.of("message","Grupo atualizado com sucesso: "));
    }

}
