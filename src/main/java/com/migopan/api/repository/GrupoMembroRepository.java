package com.migopan.api.repository;

import com.migopan.api.model.Grupo;
import com.migopan.api.model.GrupoMembro;
import com.migopan.api.model.keys.GrupoMembroId;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GrupoMembroRepository extends JpaRepository<GrupoMembro, GrupoMembroId>{
    boolean existsByGrupoIdAndUsuarioIdAndPapel(Long grupoId, Long usuarioId, String papel);

    @Query("SELECT gm FROM GrupoMembro gm JOIN FETCH gm.usuario WHERE gm.grupo.id = :grupoId")
    List <GrupoMembro> findByGrupoIdWithDetails(@Param("grupoId") Long grupoId);

    @Query("SELECT gm.grupo FROM GrupoMembro gm WHERE gm.usuario.id = :usuarioId")
    List<Grupo> findGruposByUsuarioId(@Param("usuarioId") Long usuarioId);

    List <GrupoMembro> findByGrupoId(Long grupoId);

    boolean existsByGrupoIdAndUsuarioId(Long grupoId, Long usuarioId);

    long countByGrupoId(Long grupoId);
}