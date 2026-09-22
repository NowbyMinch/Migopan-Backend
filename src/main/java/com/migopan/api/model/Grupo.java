package com.migopan.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "grupo")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Mudado para Long para padronizar com as outras entidades

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;
    
    @Column(nullable = false)
    private Integer nivel = 1; // Nível do grupo

    @Column(name = "sequencia_dias", nullable = false)
    private Integer sequenciaDias = 0; // Dias consecutivos de tarefas feitas no grupo

    @Column(nullable = false)
    private Boolean ativo = true;
    
    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();
}