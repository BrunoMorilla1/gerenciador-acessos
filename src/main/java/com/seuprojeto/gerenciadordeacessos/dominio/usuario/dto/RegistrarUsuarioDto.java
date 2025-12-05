package com.seuprojeto.gerenciadordeacessos.dominio.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para requisição de registro de novo usuário.
 * @param nome Nome completo do usuário.
 * @param email Email (será o login).
 * @param senha Senha (mínimo 8 caracteres).
 */
public record RegistrarUsuarioDto(
        @NotBlank(message = "O nome é obrigatório.")
        String nome,

        @NotBlank(message = "O email é obrigatório.")
        @Email(message = "O email deve ser válido.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
        String senha
) {
}
