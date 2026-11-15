package com.migopan.api.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import com.migopan.api.dto.MensagemDTOs.EnviarMensagemRequestDTO;
import com.migopan.api.dto.MensagemDTOs.ErroChatDTO;
import com.migopan.api.dto.MensagemDTOs.MensagemResponseDTO;
import com.migopan.api.exception.AcessoNegadoException;
import com.migopan.api.exception.NotFoundException;
import com.migopan.api.service.MensagemService;
import com.migopan.api.websocket.UsuarioPrincipal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MensagemService mensagemService;

    @MessageMapping("/grupos/{grupoId}/mensagens")
    @SendTo("/topic/grupos/{grupoId}/mensagens")
    public MensagemResponseDTO enviar(
            @DestinationVariable Long grupoId,
            @Payload EnviarMensagemRequestDTO dto,
            Principal principal) {

        if (!(principal instanceof UsuarioPrincipal usuario)) {
            throw new AcessoNegadoException("Não autenticado.");
        }
        return mensagemService.enviar(grupoId, usuario.id(), dto);
    }

    @MessageExceptionHandler
    @SendToUser("/queue/erros")
    public ErroChatDTO tratarErro(Exception ex) {
        if (ex instanceof AcessoNegadoException
                || ex instanceof NotFoundException
                || ex instanceof IllegalArgumentException) {
            return new ErroChatDTO(ex.getMessage());
        }
        log.error("Erro ao processar mensagem do chat", ex);
        return new ErroChatDTO("Não foi possível enviar a mensagem.");
    }
}
