package com.migopan.api.websocket;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.migopan.api.model.Usuario;

public class UsuarioHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        if (request.getPrincipal() instanceof Authentication auth && auth.getPrincipal() instanceof Usuario usuario) {
            return new UsuarioPrincipal(usuario.getId());
        }
        return null;
    }
}
