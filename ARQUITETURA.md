# Arquitetura do Projeto - Gerenciador de Acessos

Este documento descreve a arquitetura do projeto, as decisões de design e os padrões aplicados.

## 1. Visão Geral

O projeto foi desenvolvido seguindo os princípios de **Domain-Driven Design (DDD)** e **Clean Architecture**, garantindo alta manutenibilidade, testabilidade e separação de responsabilidades.

## 2. Camadas da Arquitetura

### 2.1. Camada de Domínio (`dominio/`)

Contém a lógica de negócio central da aplicação, independente de frameworks e infraestrutura.

*   **Entidades**: `Usuario`, `EntradaAcesso`, `BaseAuditoria`.
*   **Repositórios**: Interfaces para acesso a dados (`UsuarioRepositorio`, `EntradaAcessoRepositorio`).
*   **Serviços**: Lógica de negócio (`UsuarioServico`, `AutenticacaoServico`, `EntradaAcessoServico`, `NotificacaoServico`).
*   **DTOs**: Java Records para transferência de dados (`UsuarioDto`, `EntradaAcessoDto`, etc.).

### 2.2. Camada de API (`api/`)

Camada de apresentação que expõe a API REST.

*   **Controllers**: Mapeiam requisições HTTP para os serviços de domínio (`AutenticacaoController`, `UsuarioController`, `EntradaAcessoController`, `NotificacaoController`).
*   **Tratamento de Exceções**: `TratadorGlobalExcecoes` para respostas padronizadas de erro.

### 2.3. Camada de Infraestrutura (`infraestrutura/`)

Implementações específicas de infraestrutura.

*   **Segurança**: `FiltroJwt`, `FiltroRateLimiting`, `UsuarioDetailsServiceImpl`, `AuditorAwareImpl`.

### 2.4. Camada de Configuração (`config/`)

Configurações do Spring Boot.

*   **Segurança**: `ConfiguracaoSegurancaWeb` (Spring Security, JWT, Rate Limiting).
*   **OpenAPI**: `ConfiguracaoOpenApi` (Swagger UI).
*   **Rate Limiting**: `ConfiguracaoRateLimiting` (Bucket4j).

### 2.5. Camada de Core (`core/`)

Componentes essenciais reutilizáveis.

*   **Segurança**: `ServicoCriptografia` (AES-256-GCM), `HashSenha` (BCrypt), `ServicoToken` (JWT).
*   **Exceções**: `ExcecaoNegocio`, `ExcecaoNaoEncontrado`, `ExcecaoNaoAutorizado`.

### 2.6. Camada de Jobs (`job/`)

Lógica de agendamento e tarefas em background.

*   **Job de Expiração**: `VerificarExpiracaoSenhaJob` (executa diariamente à 01:00 AM).

## 3. Padrões de Design Aplicados

### 3.1. Domain-Driven Design (DDD)

*   **Entidades**: Representam conceitos de negócio (`Usuario`, `EntradaAcesso`).
*   **Repositórios**: Abstraem o acesso a dados.
*   **Serviços de Domínio**: Encapsulam a lógica de negócio complexa.

### 3.2. Clean Architecture

*   **Separação de Responsabilidades**: Cada camada tem uma responsabilidade única.
*   **Independência de Frameworks**: A lógica de negócio não depende de frameworks específicos.

### 3.3. SOLID Principles

*   **Single Responsibility Principle (SRP)**: Cada classe tem uma única responsabilidade.
*   **Open/Closed Principle (OCP)**: Aberto para extensão, fechado para modificação.
*   **Liskov Substitution Principle (LSP)**: Subtipos devem ser substituíveis por seus tipos base.
*   **Interface Segregation Principle (ISP)**: Interfaces específicas são melhores que interfaces gerais.
*   **Dependency Inversion Principle (DIP)**: Dependa de abstrações, não de implementações.

### 3.4. Repository Pattern

*   Abstrai o acesso a dados, permitindo trocar a implementação (ex: H2 para PostgreSQL) sem alterar a lógica de negócio.

### 3.5. Service Layer Pattern

*   Encapsula a lógica de negócio em serviços, separando-a dos controllers.

## 4. Decisões Técnicas

### 4.1. Java Records para DTOs

*   **Benefícios**: Imutabilidade, concisão, segurança de tipo.
*   **Uso**: Todos os DTOs (`UsuarioDto`, `EntradaAcessoDto`, etc.) são Java Records.

### 4.2. Criptografia AES-256-GCM

*   **Benefício**: Criptografia forte e autenticada para senhas armazenadas.
*   **Uso**: `ServicoCriptografia` criptografa e descriptografa senhas de acessos.

### 4.3. JWT para Autenticação

*   **Benefício**: Stateless, escalável, padrão de mercado.
*   **Uso**: `ServicoToken` gera e valida tokens JWT.

### 4.4. BCrypt para Senhas de Usuário

*   **Benefício**: Hash seguro e lento, resistente a ataques de força bruta.
*   **Uso**: `HashSenha` gera e verifica hashes de senhas de usuários.

### 4.5. Rate Limiting com Bucket4j

*   **Benefício**: Proteção contra ataques de força bruta e sobrecarga da API.
*   **Uso**: `FiltroRateLimiting` limita 10 requisições por minuto por IP.

### 4.6. Spring Security com @PreAuthorize

*   **Benefício**: Controle de acesso granular baseado em roles.
*   **Uso**: `@PreAuthorize("hasRole('ADMIN')")` restringe endpoints a ADMIN.

### 4.7. Auditoria JPA

*   **Benefício**: Rastreamento automático de quem criou e modificou cada entidade.
*   **Uso**: `BaseAuditoria` com `@CreatedBy`, `@CreatedDate`, `@LastModifiedBy`, `@LastModifiedDate`.

### 4.8. Job Agendado com @Scheduled

*   **Benefício**: Execução automática de tarefas em background.
*   **Uso**: `VerificarExpiracaoSenhaJob` verifica senhas expiradas diariamente.

### 4.9. Perfis de Ambiente (H2 / PostgreSQL)

*   **Benefício**: Facilita o desenvolvimento (H2) e a produção (PostgreSQL) com uma única configuração.
*   **Uso**: `application.yml` com perfis `dev` e `prod`.

## 5. Conclusão

A arquitetura do projeto foi projetada para ser **escalável**, **manutenível** e **testável**, seguindo as melhores práticas de engenharia de software de nível Sênior/Arquiteto.
