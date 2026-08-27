package com.migopan.api.dto.grupo;

import jakarta.validation.constraints.Size;

public record AtualizarGrupoRequestDTO(
    @Size(min = 2, max = 100, message = "O nome do grupo deve ter entre 2 e 100 caracteres")
    String nome,
    
    String descricao
){} 
