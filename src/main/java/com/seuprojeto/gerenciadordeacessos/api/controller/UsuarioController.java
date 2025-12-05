package com.seuprojeto.gerenciadordeacessos.api.controller;

import com.seuprojeto.gerenciadordeacessos.dominio.usuario.dto.UsuarioDto;
import com.seuprojeto.gerenciadordeacessos.dominio.usuario.servico.UsuarioServico;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller para gerenciamento de usuários.
 * Responsabilidade Única: Mapear requisições de usuários para os serviços.
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários (Apenas ADMIN)")
@PreAuthorize("hasRole('ADMIN')") // Restringe todo o controller a ADMIN
public class UsuarioController {

    private final UsuarioServico usuarioServico;

    @Operation(summary = "Lista todos os usuários ativos")
    @GetMapping
    public ResponseEntity<List<UsuarioDto>> listarTodos() {
        return ResponseEntity.ok(usuarioServico.listarTodos());
    }

    @Operation(summary = "Busca um usuário pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioServico.buscarPorId(id));
    }
}
