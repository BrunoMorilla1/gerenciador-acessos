package com.seuprojeto.gerenciadordeacessos.dominio.usuario.repositorio;

import com.seuprojeto.gerenciadordeacessos.dominio.usuario.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para a entidade Usuario.
 * Responsabilidade Única: Acesso a dados de Usuario.
 */
@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    /**
     * Busca um usuário pelo email (usado como login).
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica se um email já está cadastrado.
     */
    boolean existsByEmail(String email);
}
