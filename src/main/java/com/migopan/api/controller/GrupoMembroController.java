package com.migopan.api.controller;

import com.migopan.api.dto.AtualizarMembroRequestDTO;

import java.util.List;
import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.migopan.api.dto.GrupoMembroRequestDTO;
import com.migopan.api.dto.GrupoMembroResponseDTO;
import com.migopan.api.service.GrupoMembroService;
import com.migopan.api.model.Usuario;

@RestController
@RequestMapping("/api/grupos")
public class GrupoMembroController {
    @Autowired
    private GrupoMembroService grupoMembroService;

    
    @GetMapping("/{grupoId}/membros")
    public ResponseEntity<List<GrupoMembroResponseDTO>> listarMembros(@PathVariable Long grupoId) {
        List<GrupoMembroResponseDTO> membros = grupoMembroService.getMembrosPorGrupo(grupoId);
        return ResponseEntity.ok(membros);
    }

    @PostMapping("/{grupoId}/membros")
    public ResponseEntity<String> adicionarMembro (
            @PathVariable Long grupoId, 
            @AuthenticationPrincipal usuarioIdAdminLogado,
            @RequestBody @Valid GrupoMembroRequestDTO dto){
        
        GrupoMembroResponseDTO novoMembro = grupoMembroService.adicionarMembro(grupoId, usuarioLogado.getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoMembro);
    }

    @PatchMapping("/{grupoId}/membros/{usuarioIdMembro}")
    public ResponseEntity<String> atualizarMembro(
            @PathVariable Long grupoId,
            @PathVariable Long usuarioIdMembro,
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody @Valid AtualizarMembroRequestDTO dto){
            
        String mensagem = grupoMembroService.atualizarMembro(grupoId, usuario.getId(), usuarioIdMembro, dto)
        return ResponseEntity.ok(mensagem);
    }

    @DeleteMapping("/{grupoId}/membros/{usuarioIdMembro}")
    public ResponseEntity<String> removerMembro(
            @PathVariable Long grupoId, 
            @AuthenticationPrincipal Usuario usuarioLogado,
            @PathVariable Long usuarioIdMembro) {

        String mensagem = grupoMembroService.removerMembro(grupoId, usuarioLogado.getId(), usuarioIdMembro);

        return ResponseEntity.ok(mensagem);
    }

}