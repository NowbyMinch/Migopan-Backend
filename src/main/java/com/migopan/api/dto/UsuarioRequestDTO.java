package com.migopan.api.dto;

import jakarta.validation.constraints.NotBlank;

public record UsuarioRequestDTO(
    @NotBlank(message="O nome é obrigatório")
    String nome,
    String descricao
) {}

// @Id
// @GeneratedValue(strategy = GenerationType.IDENTITY)
// private Long id;

// @Column(nullable = false, length = 100)
// private String nome;

// @Column(nullable = false, unique = true, length = 255)
// private String email;

// @Column(nullable = false, name = "email_verificado")
// private Boolean emailVerificado;

// @Column(nullable = false, length = 255)
// private String senha_hash;

// @Column(nullable = false)
// private Integer streak;

// @Column(nullable = false, precision = 12, scale = 2)
// private BigDecimal dinheiro = BigDecimal.ZERO;

// @Column(nullable = false, name = "data_criacao", updatable = false)
// private LocalDateTime dataCriacao = LocalDateTime.now();
    
// @Column(nullable = false)
// private Boolean ativo = true;