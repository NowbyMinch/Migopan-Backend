package com.migopan.api.model;

import java.time.LocalDateTime;

import com.migopan.api.model.enums.StatusAmizade;
import com.migopan.api.model.keys.ListaAmigosId;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lista_amigos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ListaAmigos {

    @EmbeddedId
    private ListaAmigosId id = new ListaAmigosId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuario")
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("amigo")
    @JoinColumn(name = "amigo_id")
    private Usuario amigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_amizade", nullable = false, length = 20)
    private StatusAmizade statusAmizade = StatusAmizade.PENDENTE;

    @Column(name = "data_amizade", nullable = false)
    private LocalDateTime dataAmizade;
}
