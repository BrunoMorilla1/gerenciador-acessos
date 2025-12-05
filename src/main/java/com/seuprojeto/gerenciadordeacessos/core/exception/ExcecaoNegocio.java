package com.seuprojeto.gerenciadordeacessos.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção de Negócio. Deve ser lançada quando uma regra de negócio é violada.
 * Mapeada para o status HTTP 400 (Bad Request).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ExcecaoNegocio extends RuntimeException {
    public ExcecaoNegocio(String message) {
        super(message);
    }
}
