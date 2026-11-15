package com.migopan.api.websocket;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import com.migopan.api.repository.GrupoMembroRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StompAuthInterceptor implements ChannelInterceptor {

    private static final Pattern TOPICO_DO_GRUPO = Pattern.compile("^/topic/grupos/(\\d{1,18})/mensagens$");
    private static final Pattern ENVIO_PARA_O_GRUPO = Pattern.compile("^/app/grupos/(\\d{1,18})/mensagens$");
    private static final String FILA_PRIVADA_DE_ERROS = "/user/queue/erros";

    private final GrupoMembroRepository grupoMembroRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        StompCommand comando = accessor.getCommand();
        if (comando == StompCommand.CONNECT) {
            idDoUsuario(accessor);
        } else if (comando == StompCommand.SUBSCRIBE) {
            autorizarInscricao(accessor);
        } else if (comando == StompCommand.SEND) {
            autorizarEnvio(accessor);
        }
        return message;
    }

    private void autorizarInscricao(StompHeaderAccessor accessor) {
        Long usuarioId = idDoUsuario(accessor);
        String destino = accessor.getDestination();

        if (FILA_PRIVADA_DE_ERROS.equals(destino)) {
            return;
        }

        Matcher m = destino == null ? null : TOPICO_DO_GRUPO.matcher(destino);
        if (m == null || !m.matches()) {
            throw negar("Destino não permitido.");
        }

        Long grupoId = Long.valueOf(m.group(1));
        if (!grupoMembroRepository.existsByGrupoIdAndUsuarioIdAndBloqueadoFalse(grupoId, usuarioId)) {
            throw negar("Você não faz parte deste grupo.");
        }
    }

    private void autorizarEnvio(StompHeaderAccessor accessor) {
        idDoUsuario(accessor);
        String destino = accessor.getDestination();

        if (destino == null || !ENVIO_PARA_O_GRUPO.matcher(destino).matches()) {
            throw negar("Destino de envio não permitido.");
        }
    }

    private Long idDoUsuario(StompHeaderAccessor accessor) {
        if (accessor.getUser() instanceof UsuarioPrincipal principal) {
            return principal.id();
        }
        throw negar("Não autenticado.");
    }

    private MessagingException negar(String motivo) {
        return new MessagingException(motivo);
    }
}
