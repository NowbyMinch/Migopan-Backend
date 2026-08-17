package com.migopan.api.model;

import com.migopan.api.model.keys.GrupoMembroId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "grupo_membro")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class GrupoMembro {

    @EmbeddedId
    private GrupoMembroId id = new GrupoMembroId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuario_id")
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("grupo_id")
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;
    
    @Column(nullable = false, length = 20)
    private String papel = "MEMBRO"; // ADMIN, MEMBRO

    @Column(name = "data_entrada", nullable = false)
    private LocalDateTime dataEntrada = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean bloqueado = false;
}