package com.seuprojeto.gerenciadordeacessos.job;

import com.seuprojeto.gerenciadordeacessos.dominio.acesso.modelo.EntradaAcesso;
import com.seuprojeto.gerenciadordeacessos.dominio.acesso.repositorio.EntradaAcessoRepositorio;
import com.seuprojeto.gerenciadordeacessos.dominio.notificacao.servico.NotificacaoServico;
import com.seuprojeto.gerenciadordeacessos.dominio.notificacao.servico.NotificacaoServico.TipoNotificacao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Job Agendado para verificar a expiração de senhas.
 * Responsabilidade Única: Executar a lógica de verificação de expiração em background.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VerificarExpiracaoSenhaJob {

    private final EntradaAcessoRepositorio acessoRepositorio;
    private final NotificacaoServico notificacaoServico;

    @Value("${job.expiracao.dias-alerta:7}")
    private int diasAlertaExpiracao;

    /**
     * Executa a verificação de expiração diariamente à 01:00 AM.
     * Cron: "0 0 1 * * *" (Segundos, Minutos, Horas, Dia do Mês, Mês, Dia da Semana)
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void verificarExpiracao() {
        log.info("JOB: Iniciando verificação de expiração de senhas...");

        LocalDate hoje = LocalDate.now();
        LocalDate dataLimiteAlerta = hoje.plusDays(diasAlertaExpiracao);

        // 1. Busca todas as entradas que expiram até a data limite de alerta
        List<EntradaAcesso> acessosCriticos = acessoRepositorio.findExpirandoAte(dataLimiteAlerta);

        if (acessosCriticos.isEmpty()) {
            log.info("JOB: Nenhuma senha crítica encontrada.");
            return;
        }

        log.warn("JOB: Encontrados {} acessos críticos (expirados ou próximos de expirar).", acessosCriticos.size());

        // 2. Processa e gera notificações
        for (EntradaAcesso acesso : acessosCriticos) {
            String mensagem;
            TipoNotificacao tipo;

            if (acesso.getDataExpiracao().isBefore(hoje) || acesso.getDataExpiracao().isEqual(hoje)) {
                // Senha Expirada (CRÍTICO)
                mensagem = String.format("CRÍTICO: A senha do acesso '%s' (Proprietário: %s) expirou em %s.",
                        acesso.getTitulo(), acesso.getProprietario().getNome(), acesso.getDataExpiracao());
                tipo = TipoNotificacao.CRITICO;
            } else {
                // Senha Próxima de Expirar (ALERTA)
                long diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(hoje, acesso.getDataExpiracao());
                mensagem = String.format("ALERTA: A senha do acesso '%s' (Proprietário: %s) expira em %d dias (%s).",
                        acesso.getTitulo(), acesso.getProprietario().getNome(), diasRestantes, acesso.getDataExpiracao());
                tipo = TipoNotificacao.ALERTA;
            }

            notificacaoServico.adicionarNotificacao(tipo, mensagem);
        }

        log.info("JOB: Verificação de expiração concluída. Notificações geradas.");
    }
}
