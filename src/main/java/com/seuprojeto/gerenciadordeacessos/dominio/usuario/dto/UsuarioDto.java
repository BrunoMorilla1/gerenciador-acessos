package com.seuprojeto.gerenciadordeacessos.dominio.usuario.dto;

import com.seuprojeto.gerenciadordeacessos.dominio.usuario.modelo.Usuario;

/**
 * DTO para exibição de informações do usuário.
 * @param id ID do usuário.
 * @param nome Nome completo.
 * @param email Email.
 * @param role Perfil de acesso.
 */
public record UsuarioDto(
        Long id,
        String nome,
        String email,
        String role
) {
    public static UsuarioDto fromEntity(Usuario usuario) {
        return new UsuarioDto(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole().name()
        );
    }
}
