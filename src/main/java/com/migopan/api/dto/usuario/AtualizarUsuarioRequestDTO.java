package com.migopan.api.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record AtualizarUsuarioRequestDTO(
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    String nome,

    @Email(message = "Formato de email inválido")
    @Size(max = 255, message = "O email deve ter no máximo 255 caracteres")
    String email
) {}