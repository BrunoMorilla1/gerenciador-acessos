package com.seuprojeto.gerenciadordeacessos.dominio.usuario.dto;

/**
 * DTO para resposta de login bem-sucedido.
 * @param token Token JWT gerado.
 * @param nome Nome do usuário.
 * @param email Email do usuário.
 * @param role Perfil de acesso do usuário.
 */
public record LoginRespostaDto(
        String token,
        String nome,
        String email,
        String role
) {
}
