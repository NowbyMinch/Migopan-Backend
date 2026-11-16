package com.migopan.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.migopan.api.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
            SELECT u FROM Usuario u
            WHERE LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
              AND u.id <> :usuarioLogadoId
              AND u.ativo = true
            """)
    List<Usuario> buscarPorNomeExcluindoUsuarioLogado(
            @Param("nome") String nome,
                    @Param("usuarioLogadoId") Long usuarioLogadoId);
            
                    

                    @Query("""
            SELECT u FROM Usuario u
            WHERE LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
              AND u.ativo = true
              AND NOT EXISTS (
                    SELECT gm FROM GrupoMembro gm
                    WHERE gm.usuario.id = u.id AND gm.grupo.id = :grupoId)
            ORDER BY u.nome
            """)
    List<Usuario> buscarCandidatosAoGrupo(
            @Param("nome") String nome,
            @Param("grupoId") Long grupoId,
            Pageable pagina);
}
