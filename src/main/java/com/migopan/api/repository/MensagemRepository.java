package com.migopan.api.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.migopan.api.model.Mensagem;

public interface MensagemRepository extends JpaRepository<Mensagem, Long> {

    @EntityGraph(attributePaths = "usuario")
    List<Mensagem> findByGrupoIdOrderByIdDesc(Long grupoId, Pageable pageable);

    @EntityGraph(attributePaths = "usuario")
    List<Mensagem> findByGrupoIdAndIdLessThanOrderByIdDesc(Long grupoId, Long antesDeId, Pageable pageable);
}
