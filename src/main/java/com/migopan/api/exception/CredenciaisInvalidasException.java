package com.migopan.api.exception;

public class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException() {
        super("Credenciais inválidas");
    }
}
