package com.migopan.api.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tarefa")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Tarefa {
    
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_criador_id", nullable = false)
    private Usuario usuarioCriador;

    // Preenchido quando a tarefa é PESSOAL. Nulo quando é de grupo.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_atribuido_id")
    private Usuario usuarioAtribuido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    @Column(nullable = false, length = 150)
    private String titulo;
    
    @Column(columnDefinition = "TEXT")
    private String descricao;
    
    @Column(nullable = false, length = 20)
    private String repeticao = "NENHUMA";
    
    @Column(name = "horario_resolucao")
    private LocalTime horarioResolucao;
    
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();
    
    @Column(name = "data_resolucao")
    private LocalDateTime dataResolucao;
    
    @Column(nullable = false)
    private Boolean concluida = false;

}
