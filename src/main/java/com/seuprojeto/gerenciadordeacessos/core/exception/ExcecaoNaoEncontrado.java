package com.seuprojeto.gerenciadordeacessos.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção de Recurso Não Encontrado.
 * Mapeada para o status HTTP 404 (Not Found).
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ExcecaoNaoEncontrado extends RuntimeException {
    public ExcecaoNaoEncontrado(String message) {
        super(message);
    }
}
