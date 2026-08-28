package com.migopan.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.migopan.api.model.Tarefa;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long>{
    List<Tarefa> findByUsuarioAtribuidoId(Long usuarioId);
    
    List<Tarefa> findByUsuarioAtribuidoIdAndConcluida(Long usuarioId, Boolean concluida);

    List<Tarefa> findByGrupoId(long grupoId);

    List<Tarefa> findByGrupoIdAndConcluida(long grupoId, Boolean concluida);
}
