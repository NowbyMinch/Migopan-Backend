package com.migopan.api.controller;

import com.migopan.api.dto.ListaAmigosDTOs.*;

import java.util.List;
import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.migopan.api.model.Usuario;
import com.migopan.api.service.ListaAmigosService;

@RestController
@RequestMapping("/api/amigos")
public class ListaAmigosController {
    @Autowired
    private ListaAmigosService listaAmigosService;

    // enviarSolicitacao
    @PostMapping("/solicitar/{amigoId}")
    public ResponseEntity<Void> enviarSolicitacao(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long amigoId) {
        
        listaAmigosService.enviarSolicitacao(usuario.getId(), amigoId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/status/{amigoId}")
    public ResponseEntity<Void> atualizarStatusAmizade(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long amigoId,
            @RequestBody @Valid AtualizarStatusAmizadeRequestDTO dto) {

        listaAmigosService.atualizarStatusAmizade(usuario.getId(), amigoId, dto.status());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<AmizadeResponseDTO>> listarAmigos(@AuthenticationPrincipal Usuario usuario) {
        List<AmizadeResponseDTO> amigos = listaAmigosService.listarAmigos(usuario.getId()); 
        return ResponseEntity.ok(amigos);
    }

    @GetMapping("/pendentes/recebidas")
    public ResponseEntity<List<AmizadeResponseDTO>> listarSolicitacoesRecebidas(@AuthenticationPrincipal Usuario usuario) {
        List<AmizadeResponseDTO> solicitacoes = listaAmigosService.listarSolicitacoesRecebidas(usuario.getId()); 
        return ResponseEntity.ok(solicitacoes);
    }

    @GetMapping("/pendentes/enviadas")
    public ResponseEntity<List<AmizadeResponseDTO>> listarSolicitacoesEnviadas(@AuthenticationPrincipal Usuario usuario) {
        List<AmizadeResponseDTO> solicitacoes = listaAmigosService.listarSolicitacoesEnviadas(usuario.getId()); 
        return ResponseEntity.ok(solicitacoes);
    }

    @DeleteMapping("/{amigoId}")
    public ResponseEntity<Void> removerAmizade(@AuthenticationPrincipal Usuario usuario, @PathVariable @Valid Long amigoId) {
        listaAmigosService.removerAmizade(usuario.getId(), amigoId); 
        return ResponseEntity.ok().build();
    }
    

    // Só continuar o controller, os DTOs estão prontos

}