# Guia de Início Rápido (QUICKSTART)

Este guia rápido explica como iniciar e testar a aplicação Gerenciador de Acessos em **5 minutos**.

## 1. Pré-requisitos

*   **Java Development Kit (JDK) 17** ou superior.
*   **Maven** (opcional, mas recomendado para rodar via linha de comando).

## 2. Rodando a Aplicação

### Opção 1: Via Maven (Recomendado)

1.  Navegue até o diretório raiz do projeto (`gerenciador-acessos`).
2.  Execute o comando:

```bash
mvn spring-boot:run
```

### Opção 2: Via JAR Executável

1.  Compile o projeto para gerar o JAR:

```bash
mvn clean package
```

2.  Execute o JAR gerado:

```bash
java -jar target/gerenciador-acessos-1.0.0.jar
```

### Opção 3: No IntelliJ IDEA

1.  Abra o projeto no IntelliJ.
2.  Localize a classe `GerenciadorDeAcessosApplication.java`.
3.  Clique com o botão direito e selecione **Run 'GerenciadorDeAcessosApplication'**.

## 3. Acessando a Aplicação

Após iniciar, a aplicação estará disponível em `http://localhost:8080`.

### 3.1. Frontend Simples (HTML/JS)

Acesse o frontend para testar o login e o CRUD de acessos:

*   **URL:** `http://localhost:8080/index.html`

### 3.2. Documentação da API (Swagger UI)

Acesse a documentação interativa da API para testar todos os endpoints:

*   **URL:** `http://localhost:8080/swagger-ui.html`

### 3.3. Console do Banco de Dados H2

Acesse o console do banco de dados em memória (apenas para desenvolvimento):

*   **URL:** `http://localhost:8080/h2-console`
*   **JDBC URL:** `jdbc:h2:mem:gerenciador_acessos`
*   **Usuário:** `sa`
*   **Senha:** (deixe em branco)

## 4. Testando as Funcionalidades

### 4.1. Login

Use os dados iniciais (criados automaticamente pelo `data.sql`):

| Email | Senha | Perfil |
| :--- | :--- | :--- |
| `admin@seuprojeto.com` | `admin123` | `ROLE_ADMIN` |
| `user@test.com` | `user123` | `ROLE_USER` |

1.  Vá para `http://localhost:8080/index.html`.
2.  Insira `user@test.com` e `user123`.
3.  Clique em **Entrar**.

### 4.2. Criar um Acesso

1.  Após o login, preencha o formulário **Novo Acesso**.
2.  Escolha a visibilidade:
    *   **PESSOAL**: Apenas você verá.
    *   **COMPARTILHADA**: Todos os usuários verão (apenas ADMIN pode criar).
3.  Clique em **Salvar Acesso**.

### 4.3. Visualizar Acessos

1.  Clique em **Atualizar Lista** na seção **Meus Acessos Visíveis**.
2.  Você verá todos os acessos que tem permissão para visualizar.

### 4.4. Revelar Senha

1.  Clique em **Revelar Senha** em um acesso.
2.  A senha descriptografada será exibida temporariamente (10 segundos).

### 4.5. Notificações de Expiração

1.  Clique em **Atualizar Notificações** na seção **Notificações de Senha**.
2.  Se houver senhas expiradas ou próximas de expirar, você verá alertas.

### 4.6. Rate Limiting

O sistema está configurado para permitir apenas **10 requisições por minuto** por IP. Tente fazer mais de 10 requisições seguidas (ex: clicando rapidamente em "Atualizar Acessos") para ver a resposta `429 Too Many Requests`.

### 4.7. Regras de Autorização

*   **Criação de Usuário**: Tente registrar um novo usuário com a conta `user@test.com` via Swagger. Você receberá um erro de `403 Forbidden`. Apenas `admin@seuprojeto.com` pode fazer isso.
*   **Criação de Acesso Compartilhado**: Tente criar um acesso com `Visibilidade: COMPARTILHADA` com a conta `user@test.com`. Você receberá um erro de `403 Forbidden`. Apenas `admin@seuprojeto.com` pode fazer isso.

## 5. Mudando para PostgreSQL (Produção)

1.  Altere o perfil ativo no `application.yml`:

```yaml
spring:
  profiles:
    active: prod
```

2.  Configure as variáveis de ambiente:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/gerenciador_acessos
export DB_USERNAME=postgres
export DB_PASSWORD=sua_senha
```

3.  Reinicie a aplicação.

## 6. Próximos Passos

*   Explore a API via Swagger UI.
*   Crie acessos pessoais e compartilhados.
*   Teste o Job de expiração (ele roda diariamente à 01:00 AM, mas você pode alterar o cron no `VerificarExpiracaoSenhaJob.java`).
*   Personalize o frontend para suas necessidades.

---

**O projeto está completo, testado e pronto para uso!** 🚀
