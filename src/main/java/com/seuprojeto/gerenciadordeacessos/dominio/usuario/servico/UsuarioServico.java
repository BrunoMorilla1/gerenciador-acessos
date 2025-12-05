package com.seuprojeto.gerenciadordeacessos.dominio.usuario.servico;

import com.seuprojeto.gerenciadordeacessos.core.exception.ExcecaoNegocio;
import com.seuprojeto.gerenciadordeacessos.core.exception.ExcecaoNaoEncontrado;
import com.seuprojeto.gerenciadordeacessos.core.seguranca.HashSenha;
import com.seuprojeto.gerenciadordeacessos.dominio.usuario.dto.RegistrarUsuarioDto;
import com.seuprojeto.gerenciadordeacessos.dominio.usuario.dto.UsuarioDto;
import com.seuprojeto.gerenciadordeacessos.dominio.usuario.modelo.Usuario;
import com.seuprojeto.gerenciadordeacessos.dominio.usuario.repositorio.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de Lógica de Negócio para a entidade Usuario.
 * Responsabilidade Única: Gerenciar as regras de negócio relacionadas a usuários.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioServico {

    private final UsuarioRepositorio usuarioRepositorio;
    private final HashSenha hashSenha;

    /**
     * Registra um novo usuário no sistema.
     */
    @Transactional
    public UsuarioDto registrar(RegistrarUsuarioDto dto) {
        log.info("Tentativa de registro de novo usuário: {}", dto.email());

        if (usuarioRepositorio.existsByEmail(dto.email())) {
            throw new ExcecaoNegocio("Email já cadastrado: " + dto.email());
        }

        // Por padrão, novos usuários são ROLE_USER
        Usuario novoUsuario = Usuario.builder()
                .nome(dto.nome())
                .email(dto.email())
                .senha(hashSenha.gerarHash(dto.senha()))
                .role(Usuario.Role.ROLE_USER)
                .build();
        novoUsuario.setAtivo(true);

        Usuario salvo = usuarioRepositorio.save(novoUsuario);
        log.info("Usuário registrado com sucesso: {}", salvo.getEmail());
        return UsuarioDto.fromEntity(salvo);
    }

    /**
     * Busca um usuário pelo ID.
     */
    public UsuarioDto buscarPorId(Long id) {
        Usuario usuario = usuarioRepositorio.findById(id)
                .orElseThrow(() -> new ExcecaoNaoEncontrado("Usuário não encontrado com ID: " + id));
        return UsuarioDto.fromEntity(usuario);
    }

    /**
     * Lista todos os usuários ativos.
     */
    public List<UsuarioDto> listarTodos() {
        return usuarioRepositorio.findAll().stream()
                .filter(Usuario::getAtivo)
                .map(UsuarioDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Busca a entidade Usuario pelo email.
     */
    public Usuario buscarEntidadePorEmail(String email) {
        return usuarioRepositorio.findByEmail(email)
                .orElseThrow(() -> new ExcecaoNaoEncontrado("Usuário não encontrado com email: " + email));
    }
}
