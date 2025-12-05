package com.seuprojeto.gerenciadordeacessos.dominio.notificacao.servico;

import com.seuprojeto.gerenciadordeacessos.dominio.notificacao.dto.NotificacaoDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Serviço de Notificação em Memória.
 * Responsabilidade Única: Gerenciar a lista de notificações do sistema.
 * Utiliza CopyOnWriteArrayList para ser thread-safe (acessado pelo Job e pelo Controller).
 */
@Service
public class NotificacaoServico {

    private final CopyOnWriteArrayList<NotificacaoDto> notificacoes = new CopyOnWriteArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public enum TipoNotificacao {
        ALERTA,
        CRITICO
    }

    /**
     * Adiciona uma nova notificação à lista.
     */
    public void adicionarNotificacao(TipoNotificacao tipo, String mensagem) {
        NotificacaoDto novaNotificacao = new NotificacaoDto(
                idGenerator.incrementAndGet(),
                tipo.name(),
                mensagem,
                LocalDateTime.now()
        );
        notificacoes.add(novaNotificacao);
    }

    /**
     * Lista todas as notificações, ordenadas pela mais recente.
     */
    public List<NotificacaoDto> listarTodas() {
        return notificacoes.stream()
                .sorted(Comparator.comparing(NotificacaoDto::data).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Remove uma notificação pelo ID.
     */
    public void removerNotificacao(Long id) {
        notificacoes.removeIf(n -> n.id().equals(id));
    }

    /**
     * Limpa todas as notificações.
     */
    public void limparTodas() {
        notificacoes.clear();
    }
}
