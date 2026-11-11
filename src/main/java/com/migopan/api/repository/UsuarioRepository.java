package com.migopan.api.repository;

import java.util.List;
import java.util.Optional;

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
}
