package com.seuprojeto.gerenciadordeacessos.dominio.acesso.servico;

import com.seuprojeto.gerenciadordeacessos.core.exception.ExcecaoNaoAutorizado;
import com.seuprojeto.gerenciadordeacessos.core.exception.ExcecaoNaoEncontrado;
import com.seuprojeto.gerenciadordeacessos.core.seguranca.ServicoCriptografia;
import com.seuprojeto.gerenciadordeacessos.dominio.acesso.dto.*;
import com.seuprojeto.gerenciadordeacessos.dominio.acesso.modelo.EntradaAcesso;
import com.seuprojeto.gerenciadordeacessos.dominio.acesso.modelo.EntradaAcesso.TipoVisibilidade;
import com.seuprojeto.gerenciadordeacessos.dominio.acesso.repositorio.EntradaAcessoRepositorio;
import com.seuprojeto.gerenciadordeacessos.dominio.notificacao.servico.NotificacaoServico;
import com.seuprojeto.gerenciadordeacessos.dominio.usuario.modelo.Usuario;
import com.seuprojeto.gerenciadordeacessos.dominio.usuario.servico.UsuarioServico;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de Lógica de Negócio para a entidade EntradaAcesso.
 * Responsabilidade Única: Gerenciar as regras de negócio de credenciais.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EntradaAcessoServico {

    private final EntradaAcessoRepositorio acessoRepositorio;
    private final UsuarioServico usuarioServico;
    private final ServicoCriptografia servicoCriptografia;
    private final NotificacaoServico notificacaoServico;

    @Value("${job.expiracao.dias-alerta:7}")
    private int diasAlertaExpiracao;

    /**
     * Cria uma nova entrada de acesso.
     */
    @Transactional
    public EntradaAcessoDto criar(CriarEntradaAcessoDto dto, String emailUsuario) {
        log.info("Criando nova entrada de acesso: {} (tipo: {})", dto.titulo(), dto.tipoVisibilidade());

        Usuario proprietario = usuarioServico.buscarEntidadePorEmail(emailUsuario);

        // ** Regra de Negócio: Apenas ADMIN pode criar acessos COMPARTILHADOS **
        if (dto.tipoVisibilidade() == TipoVisibilidade.COMPARTILHADA && !proprietario.isAdmin()) {
            throw new ExcecaoNaoAutorizado("Apenas administradores podem criar acessos compartilhados.");
        }

        // Criptografa a senha antes de salvar
        String senhaCriptografada = servicoCriptografia.criptografar(dto.senha());

        EntradaAcesso entrada = EntradaAcesso.builder()
                .titulo(dto.titulo())
                .descricao(dto.descricao())
                .url(dto.url())
                .login(dto.login())
                .senhaCriptografada(senhaCriptografada)
                .tipoVisibilidade(dto.tipoVisibilidade())
                .proprietario(proprietario)
                .dataExpiracao(dto.dataExpiracao())
                .build();

        EntradaAcesso salvo = acessoRepositorio.save(entrada);
        return toDto(salvo);
    }

    /**
     * Atualiza uma entrada de acesso.
     */
    @Transactional
    public EntradaAcessoDto atualizar(Long id, AtualizarEntradaAcessoDto dto, String emailUsuario) {
        EntradaAcesso entrada = buscarEntidadePorId(id);
        Usuario usuario = usuarioServico.buscarEntidadePorEmail(emailUsuario);

        // ** Regra de Negócio: Apenas proprietário ou ADMIN pode atualizar **
        if (!entrada.getProprietario().getEmail().equals(emailUsuario) && !usuario.isAdmin()) {
            throw new ExcecaoNaoAutorizado("Você não tem permissão para atualizar este acesso.");
        }

        // ** Regra de Negócio: Apenas ADMIN pode alterar para COMPARTILHADA **
        if (dto.tipoVisibilidade() == TipoVisibilidade.COMPARTILHADA && !entrada.getTipoVisibilidade().equals(TipoVisibilidade.COMPARTILHADA) && !usuario.isAdmin()) {
            throw new ExcecaoNaoAutorizado("Apenas administradores podem alterar a visibilidade para compartilhada.");
        }

        entrada.setTitulo(dto.titulo());
        entrada.setDescricao(dto.descricao());
        entrada.setUrl(dto.url());
        entrada.setLogin(dto.login());
        entrada.setTipoVisibilidade(dto.tipoVisibilidade());
        entrada.setDataExpiracao(dto.dataExpiracao());

        // Se a senha foi fornecida, criptografa e atualiza
        if (dto.senha() != null && !dto.senha().isBlank()) {
            entrada.setSenhaCriptografada(servicoCriptografia.criptografar(dto.senha()));
        }

        EntradaAcesso salvo = acessoRepositorio.save(entrada);
        return toDto(salvo);
    }

    /**
     * Busca uma entrada por ID, verificando a permissão de visualização.
     */
    public EntradaAcessoDto buscarPorId(Long id, String emailUsuario) {
        EntradaAcesso entrada = buscarEntidadePorId(id);
        Usuario usuario = usuarioServico.buscarEntidadePorEmail(emailUsuario);

        // ** Regra de Negócio: Verifica se o usuário pode ver **
        if (entrada.getTipoVisibilidade() == TipoVisibilidade.PESSOAL && !entrada.getProprietario().getEmail().equals(emailUsuario) && !usuario.isAdmin()) {
            throw new ExcecaoNaoAutorizado("Você não tem permissão para visualizar este acesso pessoal.");
        }

        return toDto(entrada);
    }

    /**
     * Lista todas as entradas visíveis para o usuário autenticado.
     */
    public List<EntradaAcessoDto> listarVisiveisPara(String emailUsuario) {
        return acessoRepositorio.findVisiveisParaUsuario(emailUsuario).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Lista apenas as entradas compartilhadas.
     */
    public List<EntradaAcessoDto> listarCompartilhadas() {
        return acessoRepositorio.findByTipoVisibilidadeAndAtivoTrue(TipoVisibilidade.COMPARTILHADA).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Lista apenas as entradas pessoais do usuário.
     */
    public List<EntradaAcessoDto> listarPessoais(String emailUsuario) {
        return acessoRepositorio.findByProprietarioEmailAndTipoVisibilidadeAndAtivoTrue(emailUsuario, TipoVisibilidade.PESSOAL).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Revela a senha descriptografada (operação auditada).
     */
    public RevelarSenhaDto revelarSenha(Long id, String emailUsuario) {
        EntradaAcesso entrada = buscarEntidadePorId(id);
        Usuario usuario = usuarioServico.buscarEntidadePorEmail(emailUsuario);

        // ** Regra de Negócio: Verifica se o usuário pode ver **
        if (entrada.getTipoVisibilidade() == TipoVisibilidade.PESSOAL && !entrada.getProprietario().getEmail().equals(emailUsuario) && !usuario.isAdmin()) {
            throw new ExcecaoNaoAutorizado("Você não tem permissão para visualizar este acesso pessoal.");
        }

        // Descriptografa a senha
        String senhaPlana = servicoCriptografia.descriptografar(entrada.getSenhaCriptografada());

        log.warn("AUDITORIA: Usuário {} revelou a senha do acesso ID: {}", emailUsuario, id);

        return new RevelarSenhaDto(senhaPlana, "Senha revelada com sucesso (operação auditada).");
    }

    /**
     * Exclui (soft delete) uma entrada de acesso.
     */
    @Transactional
    public void excluir(Long id, String emailUsuario) {
        EntradaAcesso entrada = buscarEntidadePorId(id);
        Usuario usuario = usuarioServico.buscarEntidadePorEmail(emailUsuario);

        // ** Regra de Negócio: Apenas proprietário ou ADMIN pode excluir **
        if (!entrada.getProprietario().getEmail().equals(emailUsuario) && !usuario.isAdmin()) {
            throw new ExcecaoNaoAutorizado("Você não tem permissão para excluir este acesso.");
        }

        entrada.setAtivo(false); // Soft delete
        acessoRepositorio.save(entrada);
        log.info("Acesso ID {} excluído (soft delete) pelo usuário {}", id, emailUsuario);
    }

    /**
     * Busca entradas por título (busca parcial) visíveis para o usuário.
     */
    public List<EntradaAcessoDto> buscarPorTitulo(String titulo, String emailUsuario) {
        return acessoRepositorio.findByTituloParcialVisivelParaUsuario(titulo, emailUsuario).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca a entidade EntradaAcesso pelo ID.
     */
    private EntradaAcesso buscarEntidadePorId(Long id) {
        return acessoRepositorio.findById(id)
                .filter(EntradaAcesso::getAtivo)
                .orElseThrow(() -> new ExcecaoNaoEncontrado("Entrada de acesso não encontrada com ID: " + id));
    }

    /**
     * Converte Entidade para DTO, calculando status de expiração.
     */
    private EntradaAcessoDto toDto(EntradaAcesso entrada) {
        LocalDate hoje = LocalDate.now();
        LocalDate dataExpiracao = entrada.getDataExpiracao();

        boolean expirada = dataExpiracao != null && dataExpiracao.isBefore(hoje);
        boolean proximaExpiracao = dataExpiracao != null && dataExpiracao.isAfter(hoje) && dataExpiracao.isBefore(hoje.plusDays(diasAlertaExpiracao));

        return EntradaAcessoDto.fromEntity(entrada, expirada, proximaExpiracao);
    }
}
