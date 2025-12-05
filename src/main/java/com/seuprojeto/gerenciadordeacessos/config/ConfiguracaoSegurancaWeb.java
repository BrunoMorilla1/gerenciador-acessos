package com.seuprojeto.gerenciadordeacessos.config;

import com.seuprojeto.gerenciadordeacessos.infraestrutura.seguranca.FiltroJwt;
import com.seuprojeto.gerenciadordeacessos.infraestrutura.seguranca.FiltroRateLimiting;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuração de Segurança do Spring Security.
 * Nível Sênior: Uso de Beans, Configuração Stateless e Filtros Customizados.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Habilita @PreAuthorize
@RequiredArgsConstructor
public class ConfiguracaoSegurancaWeb {

    private final FiltroJwt filtroJwt;
    private final FiltroRateLimiting filtroRateLimiting;
    private final UserDetailsService userDetailsService;

    private static final String[] ENDPOINTS_PUBLICOS = {
            "/api/auth/login",
            "/api/auth/registrar", // Restrito internamente por @PreAuthorize
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/h2-console/**",
            "/index.html",
            "/app.js"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita CSRF (necessário para APIs stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // Configura as permissões de requisição
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ENDPOINTS_PUBLICOS).permitAll()
                        .anyRequest().authenticated()
                )

                // Configura a gestão de sessão para ser stateless (sem estado)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Adiciona o provedor de autenticação
                .authenticationProvider(authenticationProvider())

                // Adiciona o filtro de Rate Limiting ANTES do filtro JWT
                .addFilterBefore(filtroRateLimiting, UsernamePasswordAuthenticationFilter.class)

                // Adiciona o filtro JWT ANTES do filtro de autenticação padrão
                .addFilterBefore(filtroJwt, UsernamePasswordAuthenticationFilter.class)

                // Permite o uso do console H2 (apenas para perfil dev)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
