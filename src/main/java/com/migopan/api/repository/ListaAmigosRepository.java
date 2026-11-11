package com.migopan.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.migopan.api.model.ListaAmigos;
import com.migopan.api.model.enums.StatusAmizade;
import com.migopan.api.model.keys.ListaAmigosId;

public interface ListaAmigosRepository extends JpaRepository<ListaAmigos, ListaAmigosId> {

    @Query("""
            SELECT l FROM ListaAmigos l
            JOIN FETCH l.usuario JOIN FETCH l.amigo
            WHERE (l.usuario.id = :id OR l.amigo.id = :id) AND l.statusAmizade = :status
            """)
    List<ListaAmigos> findDoUsuarioPorStatus(@Param("id") Long usuarioId, @Param("status") StatusAmizade status);

    @Query("""
            SELECT l FROM ListaAmigos l
            JOIN FETCH l.usuario JOIN FETCH l.amigo
            WHERE l.amigo.id = :id AND l.statusAmizade = :status
            """)
    List<ListaAmigos> findRecebidasPorStatus(@Param("id") Long usuarioId, @Param("status") StatusAmizade status);

    @Query("""
            SELECT l FROM ListaAmigos l
            JOIN FETCH l.usuario JOIN FETCH l.amigo
            WHERE l.usuario.id = :id AND l.statusAmizade = :status
            """)
    List<ListaAmigos> findEnviadasPorStatus(@Param("id") Long usuarioId, @Param("status") StatusAmizade status);

    /** Relação entre dois usuários, nos dois sentidos. */
    @Query("""
            SELECT l FROM ListaAmigos l
            WHERE (l.usuario.id = :a AND l.amigo.id = :b) OR (l.usuario.id = :b AND l.amigo.id = :a)
            """)
    List<ListaAmigos> findRelacaoEntre(@Param("a") Long a, @Param("b") Long b);
}
