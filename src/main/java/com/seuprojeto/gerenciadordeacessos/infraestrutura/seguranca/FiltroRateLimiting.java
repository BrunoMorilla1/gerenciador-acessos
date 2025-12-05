package com.seuprojeto.gerenciadordeacessos.infraestrutura.seguranca;

import com.seuprojeto.gerenciadordeacessos.config.ConfiguracaoRateLimiting;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Filtro de Rate Limiting (Limitação de Taxa).
 * Responsabilidade Única: Interceptar requisições e aplicar a limitação de taxa por IP.
 */
@Component
@RequiredArgsConstructor
public class FiltroRateLimiting extends OncePerRequestFilter {

    private final ConfiguracaoRateLimiting configuracaoRateLimiting;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Ignora endpoints públicos (autenticação, swagger, static)
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth") || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") || path.startsWith("/h2-console") || path.startsWith("/index.html") || path.startsWith("/app.js")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Obtém o IP do cliente (considerando proxies)
        String ipCliente = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                .map(header -> header.split(",")[0].trim())
                .orElse(request.getRemoteAddr());

        Bucket bucket = configuracaoRateLimiting.resolverBucket(ipCliente);

        if (bucket.tryConsume(1)) {
            // Requisição permitida
            filterChain.doFilter(request, response);
        } else {
            // Requisição bloqueada
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Limite de requisições excedido. Tente novamente em breve.");
            response.getWriter().flush();
        }
    }
}
