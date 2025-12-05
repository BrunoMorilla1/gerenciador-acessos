package com.seuprojeto.gerenciadordeacessos.dominio.usuario.servico;

import com.seuprojeto.gerenciadordeacessos.core.seguranca.ServicoToken;
import com.seuprojeto.gerenciadordeacessos.dominio.usuario.dto.LoginRequisicaoDto;
import com.seuprojeto.gerenciadordeacessos.dominio.usuario.dto.LoginRespostaDto;
import com.seuprojeto.gerenciadordeacessos.dominio.usuario.modelo.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Serviço de Lógica de Negócio para Autenticação.
 * Responsabilidade Única: Gerenciar o processo de login e geração de token.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AutenticacaoServico {

    private final AuthenticationManager authenticationManager;
    private final ServicoToken servicoToken;
    private final UsuarioServico usuarioServico;

    /**
     * Autentica o usuário e gera o token JWT.
     */
    public LoginRespostaDto autenticar(LoginRequisicaoDto dto) {
        log.info("Tentativa de autenticação para o usuário: {}", dto.email());

        // 1. Autentica o usuário via Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.senha())
        );

        // 2. Obtém os detalhes do usuário autenticado
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioServico.buscarEntidadePorEmail(userDetails.getUsername());

        // 3. Gera o token JWT
        String token = servicoToken.gerarToken(userDetails);

        log.info("Autenticação bem-sucedida para o usuário: {}", usuario.getEmail());

        return new LoginRespostaDto(
                token,
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole().name()
        );
    }
}
