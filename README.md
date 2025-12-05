# Gerenciador de Acessos - Projeto Enterprise-Grade

Este é um projeto de gerenciamento de credenciais (logins e senhas) construído com **Java 17** e **Spring Boot 3**, seguindo os princípios de **Domain-Driven Design (DDD)** e **Clean Architecture**.

## 🚀 Funcionalidades Principais

1.  **Autenticação Segura**: Login com JWT e senhas hasheadas com BCrypt.
2.  **Gerenciamento de Usuários**: CRUD básico de usuários (apenas ADMIN).
3.  **Gerenciamento de Acessos (Credenciais)**:
    *   **Criptografia AES-256-GCM**: Todas as senhas são armazenadas criptografadas.
    *   **Controle de Visibilidade**: Acessos podem ser `PESSOAL` (apenas o criador vê) ou `COMPARTILHADA` (todos os usuários veem).
    *   **CRUD Completo**: Endpoints para criar, ler, atualizar e excluir acessos.
4.  **Auditoria**: Rastreamento de quem criou e modificou cada acesso.
5.  **Job de Expiração de Senha**:
    *   Um Job agendado verifica diariamente senhas expiradas ou próximas da expiração.
    *   Gera notificações para o frontend.
6.  **Notificações**: Serviço em memória para armazenar alertas do sistema.
7.  **Documentação**: OpenAPI (Swagger UI) para todos os endpoints.
8.  **Rate Limiting**: Limitação de taxa de requisições por IP (10 requisições/minuto).
9.  **Frontend Simples**: Interface HTML/JS (Vanilla) para consumo da API.

## 🛠️ Tecnologias Utilizadas

*   **Linguagem**: Java 17
*   **Framework**: Spring Boot 3.2.0
*   **Banco de Dados**: H2 (em memória, para desenvolvimento) / PostgreSQL (produção)
*   **Segurança**: Spring Security, JWT, BCrypt, AES-256-GCM
*   **Padrões**: DDD, Clean Architecture, **Java Records** (para DTOs)
*   **Build**: Maven

## ⚙️ Como Rodar o Projeto

### Pré-requisitos

*   Java 17+
*   Maven (opcional, mas recomendado)

### 1. Clonar o Repositório

```bash
git clone [URL_DO_REPOSITORIO]
cd gerenciador-acessos
```

### 2. Rodar a Aplicação

O projeto está configurado para rodar com o banco de dados H2 em memória por padrão, o que facilita o início rápido.

```bash
# Compilar e rodar
mvn spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

### 3. Acessar a Documentação da API

Acesse o Swagger UI para testar todos os endpoints:

*   **Swagger UI**: `http://localhost:8080/swagger-ui.html`
*   **Frontend Simples**: `http://localhost:8080/index.html`
*   **Console H2**: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:gerenciador_acessos`, User: `sa`, Password: deixe em branco)

## 🔑 Regras de Autorização

*   **ADMIN**: Pode criar usuários, criar acessos `PESSOAL` e `COMPARTILHADA`, e tem acesso total.
*   **USER**: Pode criar acessos apenas `PESSOAL`, e visualizar seus acessos `PESSOAL` e todos os `COMPARTILHADA`.

## 🔐 Usuários Padrão (Criados automaticamente no `data.sql`)

| Email | Senha | Perfil |
| :--- | :--- | :--- |
| `admin@seuprojeto.com` | `admin123` | `ROLE_ADMIN` |
| `user@test.com` | `user123` | `ROLE_USER` |

## 🗄️ Configuração de Banco de Dados

### Perfil de Desenvolvimento (H2 - Padrão)

O perfil `dev` está ativo por padrão e usa o banco de dados H2 em memória.

```yaml
spring:
  profiles:
    active: dev
```

### Perfil de Produção (PostgreSQL)

Para usar o PostgreSQL, altere o perfil ativo no `application.yml` para `prod` e configure as variáveis de ambiente:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/gerenciador_acessos
export DB_USERNAME=postgres
export DB_PASSWORD=sua_senha
```

Ou altere diretamente no `application.yml`:

```yaml
spring:
  profiles:
    active: prod
```

## 📚 Estrutura do Projeto

```
gerenciador-acessos/
├── src/
│   ├── main/
│   │   ├── java/com/seuprojeto/gerenciadordeacessos/
│   │   │   ├── api/                    # Controllers e Tratamento de Exceções
│   │   │   ├── config/                 # Configurações (Security, OpenAPI, Rate Limiting)
│   │   │   ├── core/                   # Componentes essenciais (Segurança, Criptografia, Exceções)
│   │   │   ├── dominio/                # Lógica de Negócio (Entidades, Repositórios, Serviços, DTOs)
│   │   │   ├── infraestrutura/         # Implementações de Infraestrutura (Filtros, Auditoria)
│   │   │   ├── job/                    # Jobs Agendados (Verificação de Expiração)
│   │   │   └── GerenciadorDeAcessosApplication.java
│   │   └── resources/
│   │       ├── application.yml         # Configuração com perfis H2/PostgreSQL
│   │       ├── data.sql                # Dados iniciais (usuários padrão)
│   │       └── static/                 # Frontend (index.html, app.js)
│   └── test/                           # Testes (não implementados nesta versão)
└── pom.xml
```

## 🧪 Testando a Aplicação

### Via Frontend

1.  Acesse `http://localhost:8080/index.html`.
2.  Faça login com `user@test.com` / `user123` ou `admin@seuprojeto.com` / `admin123`.
3.  Crie, visualize e gerencie acessos.
4.  Veja as notificações de expiração de senha.

### Via Swagger UI

1.  Acesse `http://localhost:8080/swagger-ui.html`.
2.  Clique em **Authorize** e insira o token JWT obtido no login.
3.  Teste todos os endpoints diretamente.

## 📝 Licença

Este projeto é de uso educacional e demonstrativo.
