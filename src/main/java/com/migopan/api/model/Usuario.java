package com.migopan.api.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter @Setter 
@NoArgsConstructor @AllArgsConstructor
public class Usuario{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, name = "email_verificado")
    private Boolean emailVerificado;

    @Column(nullable = false, name = "senha_hash")
    private String senhaHash;

    @Column(nullable = false)
    private Integer streak = 0;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal dinheiro = BigDecimal.ZERO;

    @Column(nullable = false, name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();
     
    @Column(nullable = false)
    private Boolean ativo = true;
}