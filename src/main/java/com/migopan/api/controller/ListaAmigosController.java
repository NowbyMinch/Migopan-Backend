package com.migopan.api.controller;

import com.migopan.api.dto.ListaAmigos.*;

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
import com.migopan.api.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/ListaAmigos")
public class ListaAmigosController {
    @Autowired
    private UsuarioRepository usuarioRepository;

    // enviarSolicitacao
    @PostMapping("/solicitar/{id}")
    public ResponseEntity<Void> enviarSolicitacao(@AuthenticationPrincipal Usuario usuario, @PathVariable Long amigoId) {
        listaAmigosRepository.enviarSolicitacao(usuario.getId(), amigoId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping
    public ResponseEntity<Void> atualizarStatusAmizade(@AuthenticationPrincipal Usuario usuario, Long amigoId, AtualizarStatusAmizadeRequestDTO dto){

    }

    // Só continuar o controller, os DTOs estão prontos

}