package com.migopan.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.migopan.api.dto.TarefaDTOs.*;
import com.migopan.api.model.Usuario;
import com.migopan.api.service.TarefaService;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/tarefas")
@CrossOrigin(origins = "*")
public class TarefaController {
    @Autowired
    private TarefaService tarefaService;

    @PostMapping
    public ResponseEntity<TarefaResponseDTO> criar(@AuthenticationPrincipal Usuario usuarioLogado,
            @RequestBody @Valid CriarTarefaRequestDTO dto) {
        TarefaResponseDTO tarefa = tarefaService.criarTarefa(usuarioLogado, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(tarefa);
    }

    @GetMapping("/pessoais")
    public ResponseEntity<List<TarefaResponseDTO>> listarPessoais(@AuthenticationPrincipal Usuario usuarioLogado,
            @RequestParam(required = false) Boolean concluida) {

        List<TarefaResponseDTO> tarefas = tarefaService.listarTarefasPessoais(usuarioLogado, concluida);
        return ResponseEntity.ok(tarefas);
    }
    
    @GetMapping("/grupo/{grupoId}")
    public ResponseEntity<List<TarefaResponseDTO>> listarPorGrupo(@PathVariable Long grupoId,
            @AuthenticationPrincipal Usuario usuarioLogado, @RequestParam(required = false) Boolean concluida) {
        List<TarefaResponseDTO> tarefas = tarefaService.listarTarefasPorGrupo(grupoId, usuarioLogado, concluida);
        return ResponseEntity.ok(tarefas);
    }
    
    @GetMapping("/{id}/concluir")
    public ResponseEntity<TarefaResponseDTO> AlterarConclusao(@PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        TarefaResponseDTO tarefa = tarefaService.AlterarConclusao(id, usuarioLogado);
        return ResponseEntity.ok(tarefa);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TarefaResponseDTO> atualizar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestBody @Valid AtualizarTarefaRequestDTO dto) {
        TarefaResponseDTO tarefa = tarefaService.atualizarTarefa(id, usuarioLogado, dto);
        return ResponseEntity.ok(tarefa);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuarioLogado) {
        String mensagem = tarefaService.deletarTarefa(id, usuarioLogado);
        return ResponseEntity.ok(mensagem);
    }

}
