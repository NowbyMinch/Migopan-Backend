package com.migopan.api.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.migopan.api.dto.MensagemDTOs.MensagemResponseDTO;
import com.migopan.api.model.Usuario;
import com.migopan.api.service.MensagemService;

import lombok.RequiredArgsConstructor;

/** Histórico via REST. O envio e o recebimento em tempo real passam pelo WebSocket. */
@RestController
@RequestMapping("/api/grupos/{grupoId}/mensagens")
@RequiredArgsConstructor
public class MensagemController {

    private final MensagemService mensagemService;

    @GetMapping
    public List<MensagemResponseDTO> listar(
            @PathVariable Long grupoId,
            @RequestParam(required = false) Long antesDe,
            @RequestParam(defaultValue = "30") int limite,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        return mensagemService.listar(grupoId, usuarioLogado.getId(), antesDe, limite);
    }
}
