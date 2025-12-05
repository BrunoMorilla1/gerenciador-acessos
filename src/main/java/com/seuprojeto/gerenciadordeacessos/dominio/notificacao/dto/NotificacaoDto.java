package com.seuprojeto.gerenciadordeacessos.dominio.notificacao.dto;

import java.time.LocalDateTime;

/**
 * DTO para exibição de notificações.
 * @param id ID da notificação.
 * @param tipo Tipo da notificação (ALERTA, CRITICO).
 * @param mensagem Mensagem da notificação.
 * @param data Data e hora da notificação.
 */
public record NotificacaoDto(
        Long id,
        String tipo,
        String mensagem,
        LocalDateTime data
) {
}
