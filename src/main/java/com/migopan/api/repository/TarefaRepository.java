package com.migopan.api.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.migopan.api.model.Tarefa;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    List<Tarefa> findByUsuarioAtribuidoId(Long usuarioId);

    List<Tarefa> findByUsuarioAtribuidoIdAndConcluida(Long usuarioId, Boolean concluida);

    List<Tarefa> findByGrupoId(Long grupoId);

    List<Tarefa> findByGrupoIdAndConcluida(Long grupoId, Boolean concluida);

    @Query("""
            SELECT t.grupo.id AS grupoId, COUNT(t) AS total
            FROM Tarefa t
            WHERE t.grupo.id IN :ids
            GROUP BY t.grupo.id
            """)
    List<ContagemPorGrupo> contarPorGrupo(@Param("ids") Collection<Long> ids);
}
