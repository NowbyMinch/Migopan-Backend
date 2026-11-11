package com.migopan.api.controller;

import java.util.List;

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

import com.migopan.api.dto.ListaAmigosDTOs.AmizadeResponseDTO;
import com.migopan.api.dto.ListaAmigosDTOs.AtualizarStatusAmizadeRequestDTO;
import com.migopan.api.dto.UsuarioDTOs.PerfilResponseDTO;
import com.migopan.api.model.Usuario;
import com.migopan.api.service.ListaAmigosService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/amigos")
@RequiredArgsConstructor
public class ListaAmigosController {

    private final ListaAmigosService listaAmigosService;

    @GetMapping("/pesquisar")
    public List<PerfilResponseDTO> pesquisar(@RequestParam String nome,
            @AuthenticationPrincipal Usuario usuario) {
        return listaAmigosService.pesquisarUsuariosParaAdicionar(nome, usuario.getId());
    }

    @GetMapping
    public List<AmizadeResponseDTO> listarAmigos(@AuthenticationPrincipal Usuario usuario) {
        return listaAmigosService.listarAmigos(usuario.getId());
    }

    @GetMapping("/pendentes/recebidas")
    public List<AmizadeResponseDTO> recebidas(@AuthenticationPrincipal Usuario usuario) {
        return listaAmigosService.listarSolicitacoesRecebidas(usuario.getId());
    }

    @GetMapping("/pendentes/enviadas")
    public List<AmizadeResponseDTO> enviadas(@AuthenticationPrincipal Usuario usuario) {
        return listaAmigosService.listarSolicitacoesEnviadas(usuario.getId());
    }

    @PostMapping("/solicitar/{amigoId}")
    public ResponseEntity<Void> solicitar(@AuthenticationPrincipal Usuario usuario, @PathVariable Long amigoId) {
        listaAmigosService.enviarSolicitacao(usuario.getId(), amigoId);
        return ResponseEntity.status(201).build();
    }

    @PatchMapping("/status/{amigoId}")
    public ResponseEntity<Void> responder(@AuthenticationPrincipal Usuario usuario,
            @PathVariable Long amigoId,
            @RequestBody @Valid AtualizarStatusAmizadeRequestDTO dto) {
        listaAmigosService.responderSolicitacao(usuario.getId(), amigoId, dto.status());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{amigoId}")
    public ResponseEntity<Void> remover(@AuthenticationPrincipal Usuario usuario, @PathVariable Long amigoId) {
        listaAmigosService.removerAmizade(usuario.getId(), amigoId);
        return ResponseEntity.noContent().build();
    }
}
