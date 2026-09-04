package com.migopan.api.repository;

import com.migopan.api.model.Usuario;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.migopan.api.dto.UsuarioDTOs.*;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND u.id != :usuarioLogadoId")
    List<Usuario> buscarPorNomeExcluindoUsuarioLogado(
        @Param("nome") String nome,
        @Param("usuarioLogadoId") Long UsuarioLogadoId
    );
}
