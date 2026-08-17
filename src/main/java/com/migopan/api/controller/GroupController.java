package com.migopan.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.migopan.api.dto.GrupoRequestDTO;
import com.migopan.api.dto.GrupoResponseDTO;
import com.migopan.api.model.Grupo;
import com.migopan.api.repository.GrupoRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/grupos")
@CrossOrigin(origins = "*")
public class GrupoController {

    @Autowired
    private GrupoRepository grupoRepository;

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Grupo> grupos = grupoRepository.findAll();
        if (grupos.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "Nenhum grupo cadastrado no momento."));
        }
        
        List<GrupoResponseDTO> response = grupos.stream().map(GrupoResponseDTO::new).toList();  
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<GrupoResponseDTO> create(@RequestBody @Valid GrupoRequestDTO dto) {
        Grupo grupo = new Grupo();
        grupo.setNome(dto.nome());
        grupo.setDescricao(dto.descricao());

        Grupo saved = grupoRepository.save(grupo);
        return ResponseEntity.status(HttpStatus.CREATED).body(new GrupoResponseDTO(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!grupoRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Grupo não encontrado com o ID: " + id));
        }

        grupoRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Grupo com ID: " + id + " deletado com sucesso."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid GrupoRequestDTO dto) {
        var optionalGrupo = grupoRepository.findById(id);

        if (optionalGrupo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Grupo não encontrado com o ID: " + id));
        }

        Grupo grupo = optionalGrupo.get();
        grupo.setNome(dto.nome());
        grupo.setDescricao(dto.descricao());
        Grupo updatedGrupo = grupoRepository.save(grupo);
        
        return ResponseEntity.ok(Map.of("message","Grupo atualizado com sucesso: " + updatedGrupo));
    }

}
