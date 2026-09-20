# Customer System - Guia Prático

Este documento complementa o `README.md` com comandos e dicas rápidas para trabalhar no projeto.

## Tecnologias

- Java 25
- Spring Boot 4.1.1
- Maven (wrapper incluso)
- H2 (banco em memória)
- Spring Data JPA + JDBC Template
- Spring Security (Basic Auth)
- WireMock (simulação do serviço externo de score)

## Build e execução

```bash
# Compilar e rodar testes
(JAVA_HOME) ./mvnw clean test

# Rodar a aplicação
(JAVA_HOME) ./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

## Autenticação

| Usuário | Senha | Papel |
|---------|-------|-------|
| user    | user  | USER  |
| admin   | admin | ADMIN |

Use Basic Auth em todas as requisições.

## Serviço externo de score

Por padrão, a aplicação inicia um **mock server WireMock** na porta `8081` para simular o serviço de score. Isso atende ao requisito do desafio de utilizar uma solução adequada (WireMock, MockServer, outra aplicação, etc.) quando o serviço real não estiver disponível.

O endpoint simulado responde em:

```
GET http://localhost:8081/scores/{cpf}
```

Exemplo de resposta:

```json
{
  "cpf": "12345678909",
  "score": 750,
  "classification": "LOW_RISK"
}
```

Para desabilitar o mock e apontar para um serviço real, ajuste em `application.properties`:

```properties
app.score-service.mock=false
app.score-service.base-url=http://score-servico-real:8080
```

## Endpoints principais

### Criar cliente (ADMIN)

```bash
curl -X POST http://localhost:8080/customers \
  -u admin:admin \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João da Silva",
    "cpf": "123.456.789-09",
    "email": "joao@email.com",
    "status": "ACTIVE"
  }'
```

### Buscar cliente por ID (USER/ADMIN)

```bash
curl http://localhost:8080/customers/1 -u user:user
```

### Buscar clientes (USER/ADMIN)

```bash
# Todos
curl "http://localhost:8080/customers" -u user:user

# Por nome
curl "http://localhost:8080/customers/search?name=joao" -u user:user

# Por status (ACTIVE ou INACTIVE)
curl "http://localhost:8080/customers?status=ACTIVE" -u user:user
```

### Atualizar cliente (ADMIN)

```bash
curl -X PUT http://localhost:8080/customers/1 \
  -u admin:admin \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João da Silva Atualizado",
    "email": "joao.novo@email.com",
    "status": "ACTIVE"
  }'
```

### Remover cliente (ADMIN)

```bash
curl -X DELETE http://localhost:8080/customers/1 -u admin:admin
```

### Consultar score (USER/ADMIN)

```bash
curl http://localhost:8080/customers/1/score -u user:user
```

## Arquitetura

O projeto segue **Arquitetura Hexagonal** + **DDD**:

```
┌─────────────────────────────────────────┐
│           Infrastructure                │
│  ┌─────────────┐    ┌────────────────┐  │
│  │  Adapters   │    │    Adapters    │  │
│  │    in       │    │     out        │  │
│  │ (REST,      │    │ (JPA/JDBC,     │  │
│  │  Security)   │    │  HTTP Score)   │  │
│  └──────┬──────┘    └───────┬────────┘  │
└─────────┼───────────────────┼───────────┘
          │                   │
┌─────────┼───────────────────┼───────────┐
│         ▼                   ▼           │
│              Application                │
│     (use cases / application services)  │
│                                         │
│  - application/port/in   (driving)      │
│  - application/service     (impl)         │
│  - application/dto       (output)       │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│                 Domain                  │
│  - domain/model    (entities, VOs)      │
│  - domain/exception                    │
│  - domain/port/out (driven ports)        │
└─────────────────────────────────────────┘
```

### Regras importantes

- **Domain** não depende de frameworks nem de `application`/`infrastructure`.
- **Application** depende apenas de `domain`.
- **Infrastructure** implementa os contratos (`ports`) e adapta tecnologias.
- Entidades de domínio (`Customer`) expõem comportamentos de negócio, não setters livres.
- Value objects (`Cpf`, `Email`) são imutáveis e auto-validáveis.
- Use cases recebem comandos e retornam DTOs da aplicação (`CustomerResult`), nunca entidades de domínio.

## Testes

```bash
# Todos os testes
(JAVA_HOME) ./mvnw test

# Apenas testes unitários de domínio
(JAVA_HOME) ./mvnw test -Dtest="*Test"
```
