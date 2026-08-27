package com.migopan.api.dto.membro;

import jakarta.validation.constraints.Pattern;

public record AtualizarMembroRequestDTO(
    @Pattern(regexp = "ADMIN|MEMBRO", message = "O papel deve ser 'ADMIN' ou 'MEMBRO'")
    String papel,
    
    Boolean bloqueado
){}