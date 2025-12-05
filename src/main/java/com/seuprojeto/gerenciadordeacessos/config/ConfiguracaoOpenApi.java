package com.seuprojeto.gerenciadordeacessos.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do OpenAPI (Swagger).
 * Nível Sênior: Configuração de segurança JWT global.
 */
@Configuration
public class ConfiguracaoOpenApi {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                )
                .info(new Info()
                        .title("Gerenciador de Acessos - API")
                        .description("API RESTful para gerenciamento seguro de credenciais (Enterprise-Grade).")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Manus - Arquiteto de Software")
                                .email("contato@manus.im")
                        )
                );
    }
}
