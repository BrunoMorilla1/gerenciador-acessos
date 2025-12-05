package com.seuprojeto.gerenciadordeacessos.dominio.acesso.repositorio;

import com.seuprojeto.gerenciadordeacessos.dominio.acesso.modelo.EntradaAcesso;
import com.seuprojeto.gerenciadordeacessos.dominio.acesso.modelo.EntradaAcesso.TipoVisibilidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositório para a entidade EntradaAcesso.
 * Responsabilidade Única: Acesso a dados de EntradaAcesso.
 */
@Repository
public interface EntradaAcessoRepositorio extends JpaRepository<EntradaAcesso, Long> {

    /**
     * Busca todas as entradas visíveis para um usuário (PESSOAL dele + COMPARTILHADA).
     */
    @Query("SELECT e FROM EntradaAcesso e WHERE e.ativo = true AND (e.proprietario.email = :email OR e.tipoVisibilidade = 'COMPARTILHADA')")
    List<EntradaAcesso> findVisiveisParaUsuario(@Param("email") String email);

    /**
     * Busca entradas pelo tipo de visibilidade.
     */
    List<EntradaAcesso> findByTipoVisibilidadeAndAtivoTrue(TipoVisibilidade tipoVisibilidade);

    /**
     * Busca entradas pessoais de um usuário.
     */
    List<EntradaAcesso> findByProprietarioEmailAndTipoVisibilidadeAndAtivoTrue(String email, TipoVisibilidade tipoVisibilidade);

    /**
     * Busca entradas ativas que estão expiradas ou próximas de expirar.
     */
    @Query("SELECT e FROM EntradaAcesso e WHERE e.ativo = true AND e.dataExpiracao IS NOT NULL AND e.dataExpiracao <= :dataLimite")
    List<EntradaAcesso> findExpirandoAte(@Param("dataLimite") LocalDate dataLimite);

    /**
     * Busca entradas por título (busca parcial) visíveis para o usuário.
     */
    @Query("SELECT e FROM EntradaAcesso e WHERE e.ativo = true AND (e.proprietario.email = :email OR e.tipoVisibilidade = 'COMPARTILHADA') AND LOWER(e.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))")
    List<EntradaAcesso> findByTituloParcialVisivelParaUsuario(@Param("titulo") String titulo, @Param("email") String email);
}
