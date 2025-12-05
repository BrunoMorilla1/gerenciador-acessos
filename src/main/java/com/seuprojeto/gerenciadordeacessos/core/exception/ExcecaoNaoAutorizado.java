package com.seuprojeto.gerenciadordeacessos.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção de Acesso Não Autorizado.
 * Mapeada para o status HTTP 403 (Forbidden).
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ExcecaoNaoAutorizado extends RuntimeException {
    public ExcecaoNaoAutorizado(String message) {
        super(message);
    }
}
