package com.seuprojeto.gerenciadordeacessos.api.controller;

import com.seuprojeto.gerenciadordeacessos.dominio.notificacao.dto.NotificacaoDto;
import com.seuprojeto.gerenciadordeacessos.dominio.notificacao.servico.NotificacaoServico;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller para gerenciamento de notificações.
 * Responsabilidade Única: Mapear requisições de notificação para os serviços.
 */
@RestController
@RequestMapping("/api/notificacoes")
@RequiredArgsConstructor
@Tag(name = "Notificações", description = "Endpoints para notificações do sistema (expiração de senha)")
@PreAuthorize("isAuthenticated()")
public class NotificacaoController {

    private final NotificacaoServico notificacaoServico;

    @Operation(summary = "Lista todas as notificações ativas (expiração de senha)")
    @GetMapping
    public ResponseEntity<List<NotificacaoDto>> listarTodas() {
        return ResponseEntity.ok(notificacaoServico.listarTodas());
    }
}
