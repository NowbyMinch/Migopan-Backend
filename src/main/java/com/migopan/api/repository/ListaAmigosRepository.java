package com.migopan.api.repository;

import com.migopan.api.dto.ListaAmigosDTOs.AmizadeResponseDTO;
import com.migopan.api.model.ListaAmigos;
import com.migopan.api.model.keys.ListaAmigosId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListaAmigosRepository extends JpaRepository<ListaAmigos, ListaAmigosId> {

    @Query("SELECT l FROM ListaAmigos l WHERE (l.usuario.id = :usuarioId OR l.amigo.id = :usuarioId) AND l.statusAmizade = 'ACEITA'")
    List<AmizadeResponseDTO> findAmizadesAceitas(@Param("usuarioId") Long usuarioId);

    @Query("SELECT l FROM ListaAmigos l WHERE l.amigo.id = :usuarioId AND l.statusAmizade = 'PENDENTE'")
    List<AmizadeResponseDTO> findSolicitacoesPendentesRecebidas(@Param("usuarioId") Long usuarioId);

    @Query("SELECT l FROM ListaAmigos l WHERE l.usuario.id = :usuarioId AND l.statusAmizade = 'PENDENTE'")
    List<AmizadeResponseDTO> findSolicitacoesPendentesEnviadas(@Param("usuarioId") Long usuarioId);

}