package com.migopan.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.migopan.api.dto.GroupRequestDTO;
import com.migopan.api.model.Group;
import com.migopan.api.repository.GroupRepository;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;




@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "*")
public class GroupController {

    @Autowired
    private GroupRepository groupRepository;

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Group> groups = groupRepository.findAll();
        if (groups.isEmpty()) {
            return ResponseEntity.ok(Map.of("messsage", "Nenhum grupo cadastrado no momento."));
        }
        
        return ResponseEntity.ok(groups);
    }

    @PostMapping
    public ResponseEntity<Group> create(@RequestBody @Valid GroupRequestDTO dto) {
        Group group = new Group();
        group.setNome(dto.nome());
        group.setDescricao(dto.descricao());
        return ResponseEntity.status(HttpStatus.CREATED).body(groupRepository.save(group));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!groupRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Grupo não encontrado com o ID: " + id));
        }

        groupRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Grupo com ID: " + id + " deletado com sucesso."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid GroupRequestDTO dto) {
        var optionalGroup = groupRepository.findById(id);

        if (optionalGroup.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Grupo não encontrado com o ID: " + id));
        }

        Group group = optionalGroup.get();
        group.setNome(dto.nome());
        group.setDescricao(dto.descricao());
        Group updatedGroup = groupRepository.save(group);
        
        return ResponseEntity.ok(Map.of("message","Grupo atualizado com sucesso: " + updatedGroup));
    }

}
