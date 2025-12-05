package com.seuprojeto.gerenciadordeacessos.dominio.acesso.dto;

import com.seuprojeto.gerenciadordeacessos.dominio.acesso.modelo.EntradaAcesso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO para atualização de uma entrada de acesso.
 * @param titulo Título da credencial.
 * @param descricao Descrição opcional.
 * @param url URL do site.
 * @param login Login/Usuário.
 * @param senha Senha em texto plano (opcional, se for alterada).
 * @param tipoVisibilidade Tipo de visibilidade (PESSOAL ou COMPARTILHADA).
 * @param dataExpiracao Data de expiração da senha (opcional).
 */
public record AtualizarEntradaAcessoDto(
        @NotBlank(message = "O título é obrigatório.")
        @Size(max = 100, message = "O título deve ter no máximo 100 caracteres.")
        String titulo,

        String descricao,

        @NotBlank(message = "A URL é obrigatória.")
        String url,

        @NotBlank(message = "O login é obrigatório.")
        String login,

        String senha, // Opcional na atualização

        EntradaAcesso.TipoVisibilidade tipoVisibilidade,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dataExpiracao
) {
}
