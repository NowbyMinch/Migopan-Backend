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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.migopan.api.dto.TarefaDTOs.AtualizarTarefaRequestDTO;
import com.migopan.api.dto.TarefaDTOs.CriarTarefaRequestDTO;
import com.migopan.api.dto.TarefaDTOs.TarefaResponseDTO;
import com.migopan.api.model.Usuario;
import com.migopan.api.service.TarefaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tarefas")
@RequiredArgsConstructor
public class TarefaController {

    private final TarefaService tarefaService;

    @PostMapping
    public ResponseEntity<TarefaResponseDTO> criar(@AuthenticationPrincipal Usuario usuarioLogado,
            @RequestBody @Valid CriarTarefaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tarefaService.criarTarefa(usuarioLogado, dto));
    }

    @GetMapping("/pessoais")
    public List<TarefaResponseDTO> listarPessoais(@AuthenticationPrincipal Usuario usuarioLogado,
            @RequestParam(required = false) Boolean concluida) {
        return tarefaService.listarTarefasPessoais(usuarioLogado, concluida);
    }

    @GetMapping("/grupo/{grupoId}")
    public List<TarefaResponseDTO> listarPorGrupo(@PathVariable Long grupoId,
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestParam(required = false) Boolean concluida) {
        return tarefaService.listarTarefasPorGrupo(grupoId, usuarioLogado, concluida);
    }

    @GetMapping("/{id}")
    public TarefaResponseDTO buscar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuarioLogado) {
        return tarefaService.buscarPorId(id, usuarioLogado);
    }

    @PatchMapping("/{id}/concluir")
    public TarefaResponseDTO alternarConclusao(@PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        return tarefaService.alterarConclusao(id, usuarioLogado);
    }

    @PatchMapping("/{id}")
    public TarefaResponseDTO atualizar(@PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestBody @Valid AtualizarTarefaRequestDTO dto) {
        return tarefaService.atualizarTarefa(id, usuarioLogado, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuarioLogado) {
        tarefaService.deletarTarefa(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
