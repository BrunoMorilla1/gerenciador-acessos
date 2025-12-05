package com.seuprojeto.gerenciadordeacessos.api.controller;

import com.seuprojeto.gerenciadordeacessos.dominio.acesso.dto.AtualizarEntradaAcessoDto;
import com.seuprojeto.gerenciadordeacessos.dominio.acesso.dto.CriarEntradaAcessoDto;
import com.seuprojeto.gerenciadordeacessos.dominio.acesso.dto.EntradaAcessoDto;
import com.seuprojeto.gerenciadordeacessos.dominio.acesso.dto.RevelarSenhaDto;
import com.seuprojeto.gerenciadordeacessos.dominio.acesso.servico.EntradaAcessoServico;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gerenciamento de entradas de acesso (credenciais).
 * Responsabilidade Única: Mapear requisições de acesso para os serviços.
 */
@RestController
@RequestMapping("/api/acessos")
@RequiredArgsConstructor
@Tag(name = "Acessos", description = "Endpoints para gerenciamento de credenciais")
@PreAuthorize("isAuthenticated()") // Garante que todos os métodos aqui exijam autenticação
public class EntradaAcessoController {

    private final EntradaAcessoServico acessoServico;

    @Operation(summary = "Cria uma nova entrada de acesso")
    @PostMapping
    public ResponseEntity<EntradaAcessoDto> criar(@RequestBody @Valid CriarEntradaAcessoDto dto, Authentication authentication) {
        EntradaAcessoDto novoAcesso = acessoServico.criar(dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAcesso);
    }

    @Operation(summary = "Atualiza uma entrada de acesso existente")
    @PutMapping("/{id}")
    public ResponseEntity<EntradaAcessoDto> atualizar(@PathVariable Long id, @RequestBody @Valid AtualizarEntradaAcessoDto dto, Authentication authentication) {
        EntradaAcessoDto acessoAtualizado = acessoServico.atualizar(id, dto, authentication.getName());
        return ResponseEntity.ok(acessoAtualizado);
    }

    @Operation(summary = "Busca uma entrada de acesso pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntradaAcessoDto> buscarPorId(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(acessoServico.buscarPorId(id, authentication.getName()));
    }

    @Operation(summary = "Lista todos os acessos visíveis para o usuário (Pessoais + Compartilhados)")
    @GetMapping
    public ResponseEntity<List<EntradaAcessoDto>> listarTodosVisiveis(Authentication authentication) {
        return ResponseEntity.ok(acessoServico.listarVisiveisPara(authentication.getName()));
    }

    @Operation(summary = "Lista apenas os acessos compartilhados (visíveis para todos)")
    @GetMapping("/compartilhados")
    public ResponseEntity<List<EntradaAcessoDto>> listarCompartilhados() {
        return ResponseEntity.ok(acessoServico.listarCompartilhadas());
    }

    @Operation(summary = "Lista apenas os acessos pessoais do usuário logado")
    @GetMapping("/pessoais")
    public ResponseEntity<List<EntradaAcessoDto>> listarPessoais(Authentication authentication) {
        return ResponseEntity.ok(acessoServico.listarPessoais(authentication.getName()));
    }

    @Operation(summary = "Busca acessos por título (busca parcial)")
    @GetMapping("/buscar")
    public ResponseEntity<List<EntradaAcessoDto>> buscarPorTitulo(@RequestParam String titulo, Authentication authentication) {
        return ResponseEntity.ok(acessoServico.buscarPorTitulo(titulo, authentication.getName()));
    }

    @Operation(summary = "Revela a senha descriptografada (operação auditada)")
    @GetMapping("/{id}/revelar")
    public ResponseEntity<RevelarSenhaDto> revelarSenha(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(acessoServico.revelarSenha(id, authentication.getName()));
    }

    @Operation(summary = "Exclui (soft delete) uma entrada de acesso")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id, Authentication authentication) {
        acessoServico.excluir(id, authentication.getName());
    }
}
