package com.seuprojeto.gerenciadordeacessos.dominio.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para requisição de login.
 * @param email Email do usuário.
 * @param senha Senha do usuário.
 */
public record LoginRequisicaoDto(
        @NotBlank(message = "O email é obrigatório.")
        @Email(message = "O email deve ser válido.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        String senha
) {
}
