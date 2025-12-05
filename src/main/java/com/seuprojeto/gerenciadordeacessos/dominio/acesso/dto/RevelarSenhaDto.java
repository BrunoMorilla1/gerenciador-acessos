package com.seuprojeto.gerenciadordeacessos.dominio.acesso.dto;

/**
 * DTO para resposta da operação de revelar senha.
 * @param senha Senha descriptografada.
 * @param mensagem Mensagem de auditoria/sucesso.
 */
public record RevelarSenhaDto(
        String senha,
        String mensagem
) {
}
