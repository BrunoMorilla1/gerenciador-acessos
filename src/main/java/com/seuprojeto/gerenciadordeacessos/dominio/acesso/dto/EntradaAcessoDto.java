package com.seuprojeto.gerenciadordeacessos.dominio.acesso.dto;

import com.seuprojeto.gerenciadordeacessos.dominio.acesso.modelo.EntradaAcesso;

import java.time.LocalDate;

/**
 * DTO para exibição de uma entrada de acesso (sem a senha criptografada).
 * @param id ID da entrada.
 * @param titulo Título.
 * @param descricao Descrição.
 * @param url URL.
 * @param login Login.
 * @param tipoVisibilidade Tipo de visibilidade.
 * @param proprietarioNome Nome do proprietário.
 * @param dataExpiracao Data de expiração.
 * @param expirada Indica se a senha está expirada.
 * @param proximaExpiracao Indica se a senha está próxima de expirar.
 */
public record EntradaAcessoDto(
        Long id,
        String titulo,
        String descricao,
        String url,
        String login,
        EntradaAcesso.TipoVisibilidade tipoVisibilidade,
        String proprietarioNome,
        LocalDate dataExpiracao,
        boolean expirada,
        boolean proximaExpiracao
) {
    public static EntradaAcessoDto fromEntity(EntradaAcesso entrada, boolean expirada, boolean proximaExpiracao) {
        return new EntradaAcessoDto(
                entrada.getId(),
                entrada.getTitulo(),
                entrada.getDescricao(),
                entrada.getUrl(),
                entrada.getLogin(),
                entrada.getTipoVisibilidade(),
                entrada.getProprietario().getNome(),
                entrada.getDataExpiracao(),
                expirada,
                proximaExpiracao
        );
    }
}
