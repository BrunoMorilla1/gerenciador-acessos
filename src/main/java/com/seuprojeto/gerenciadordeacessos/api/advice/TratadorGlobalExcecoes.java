package com.seuprojeto.gerenciadordeacessos.api.advice;

import com.seuprojeto.gerenciadordeacessos.core.exception.ExcecaoNaoAutorizado;
import com.seuprojeto.gerenciadordeacessos.core.exception.ExcecaoNaoEncontrado;
import com.seuprojeto.gerenciadordeacessos.core.exception.ExcecaoNegocio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Tratador Global de Exceções.
 * Responsabilidade Única: Capturar exceções e retornar respostas padronizadas.
 */
@RestControllerAdvice
@Slf4j
public class TratadorGlobalExcecoes {

    /**
     * Trata exceções de validação (@Valid).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("erro", "Erro de Validação");
        response.put("mensagens", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Trata ExcecaoNegocio (400 Bad Request).
     */
    @ExceptionHandler(ExcecaoNegocio.class)
    public ResponseEntity<Map<String, Object>> handleExcecaoNegocio(ExcecaoNegocio ex) {
        return criarRespostaDeErro(HttpStatus.BAD_REQUEST, "Erro de Negócio", ex.getMessage());
    }

    /**
     * Trata ExcecaoNaoEncontrado (404 Not Found).
     */
    @ExceptionHandler(ExcecaoNaoEncontrado.class)
    public ResponseEntity<Map<String, Object>> handleExcecaoNaoEncontrado(ExcecaoNaoEncontrado ex) {
        return criarRespostaDeErro(HttpStatus.NOT_FOUND, "Recurso Não Encontrado", ex.getMessage());
    }

    /**
     * Trata ExcecaoNaoAutorizado (403 Forbidden - Regra de Negócio).
     */
    @ExceptionHandler(ExcecaoNaoAutorizado.class)
    public ResponseEntity<Map<String, Object>> handleExcecaoNaoAutorizado(ExcecaoNaoAutorizado ex) {
        return criarRespostaDeErro(HttpStatus.FORBIDDEN, "Acesso Negado", ex.getMessage());
    }

    /**
     * Trata AccessDeniedException (403 Forbidden - Spring Security).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        return criarRespostaDeErro(HttpStatus.FORBIDDEN, "Acesso Negado", "Você não tem permissão para acessar este recurso.");
    }

    /**
     * Trata exceções genéricas (500 Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Erro interno do servidor: ", ex);
        return criarRespostaDeErro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno do Servidor", "Ocorreu um erro inesperado. Tente novamente mais tarde.");
    }

    private ResponseEntity<Map<String, Object>> criarRespostaDeErro(HttpStatus status, String erro, String mensagem) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("erro", erro);
        response.put("mensagem", mensagem);
        return new ResponseEntity<>(response, status);
    }
}
