package com.seuapp.model;

import com.seuapp.model.keys.ListaAmigosId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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

    @Column(name = "status_amizade", nullable = false, length = 20)
    private String statusAmizade = "PENDENTE"; // PENDENTE, ACEITA, RECUSADA

    @Column(name = "data_amizade", nullable = false)
    private LocalDateTime dataAmizade;
}