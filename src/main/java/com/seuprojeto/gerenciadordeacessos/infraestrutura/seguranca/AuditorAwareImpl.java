package com.seuprojeto.gerenciadordeacessos.infraestrutura.seguranca;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementação do AuditorAware.
 * Responsabilidade Única: Fornecer o nome do usuário logado para os campos de auditoria JPA.
 */
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.of("Sistema"); // Usuário padrão para operações não autenticadas
        }

        return Optional.of(authentication.getName()); // Retorna o email do usuário logado
    }
}
