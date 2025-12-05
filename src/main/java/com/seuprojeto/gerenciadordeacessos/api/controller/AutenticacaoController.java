package com.seuprojeto.gerenciadordeacessos.api.controller;

import com.seuprojeto.gerenciadordeacessos.dominio.usuario.dto.LoginRequisicaoDto;
import com.seuprojeto.gerenciadordeacessos.dominio.usuario.dto.LoginRespostaDto;
import com.seuprojeto.gerenciadordeacessos.dominio.usuario.dto.RegistrarUsuarioDto;
import com.seuprojeto.gerenciadordeacessos.dominio.usuario.dto.UsuarioDto;
import com.seuprojeto.gerenciadordeacessos.dominio.usuario.servico.AutenticacaoServico;
import com.seuprojeto.gerenciadordeacessos.dominio.usuario.servico.UsuarioServico;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para autenticação e registro de usuários.
 * Responsabilidade Única: Mapear requisições de login e registro para os serviços.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para Login e Registro de Usuários")
public class AutenticacaoController {

    private final AutenticacaoServico autenticacaoServico;
    private final UsuarioServico usuarioServico;

    @Operation(summary = "Realiza o login do usuário e retorna o token JWT")
    @PostMapping("/login")
    public ResponseEntity<LoginRespostaDto> login(@RequestBody @Valid LoginRequisicaoDto dto) {
        LoginRespostaDto resposta = autenticacaoServico.autenticar(dto);
        return ResponseEntity.ok(resposta);
    }

    @Operation(summary = "Registra um novo usuário (Apenas ADMIN)")
    @PostMapping("/registrar")
    @PreAuthorize("hasRole('ADMIN')") // Regra de negócio: Apenas ADMIN pode criar novos usuários
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UsuarioDto> registrar(@RequestBody @Valid RegistrarUsuarioDto dto) {
        UsuarioDto novoUsuario = usuarioServico.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }
}
