package com.migopan.api.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.migopan.api.model.Grupo;
import com.migopan.api.model.GrupoMembro;
import com.migopan.api.model.enums.PapelGrupo;
import com.migopan.api.model.keys.GrupoMembroId;

public interface GrupoMembroRepository extends JpaRepository<GrupoMembro, GrupoMembroId> {

    boolean existsByGrupoIdAndUsuarioIdAndBloqueadoFalse(Long grupoId, Long usuarioId);

    boolean existsByGrupoIdAndUsuarioIdAndPapelAndBloqueadoFalse(Long grupoId, Long usuarioId, PapelGrupo papel);

    long countByGrupoIdAndPapelAndBloqueadoFalse(Long grupoId, PapelGrupo papel);

    @Query("SELECT gm FROM GrupoMembro gm JOIN FETCH gm.usuario WHERE gm.grupo.id = :grupoId")
    List<GrupoMembro> findByGrupoIdWithDetails(@Param("grupoId") Long grupoId);

    @Query("SELECT gm.grupo FROM GrupoMembro gm WHERE gm.usuario.id = :usuarioId AND gm.bloqueado = false")
    List<Grupo> findGruposByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("""
            SELECT gm.grupo.id AS grupoId, COUNT(gm) AS total
            FROM GrupoMembro gm
            WHERE gm.grupo.id IN :ids
            GROUP BY gm.grupo.id
            """)
    List<ContagemPorGrupo> contarMembrosPorGrupo(@Param("ids") Collection<Long> ids);
}
