package com.migopan.api.repository;

import com.migopan.api.model.GrupoMembro;
import com.migopan.api.model.keys.GrupoMembroId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrupoMembroRepository extends JpaRepository<GrupoMembro, GrupoMembroId>{
    boolean existsByGrupoIdAndUsuarioIdAndPapel(Long grupoId, Long usuarioId, String papel);
}
