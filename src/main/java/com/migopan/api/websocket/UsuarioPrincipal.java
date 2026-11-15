package com.migopan.api.websocket;

import java.security.Principal;

public record UsuarioPrincipal(Long id) implements Principal {

    @Override
    public String getName() {
        return String.valueOf(id);
    }
}
