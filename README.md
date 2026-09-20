# Customer System

Microsserviço REST para gerenciamento de clientes e consulta de score, desenvolvido com Java 25, Spring Boot e Maven.

## Requisitos para execução

- Java 25
- Portas `8080` e `8081` disponíveis

O projeto inclui o Maven Wrapper, portanto não é necessário instalar o Maven.

## Como iniciar a aplicação

Na raiz do repositório, execute:

```bash
./customer-system/mvnw -f customer-system/pom.xml spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

A autenticação é HTTP Basic e utiliza os seguintes usuários em memória:

| Usuário | Senha | Perfil |
| --- | --- | --- |
| `user` | `user` | `USER` |
| `admin` | `admin` | `ADMIN` |

## Como executar os testes

Na raiz do repositório, execute:

```bash
./customer-system/mvnw -f customer-system/pom.xml test
```

Para limpar os artefatos, compilar e executar todos os testes:

```bash
./customer-system/mvnw -f customer-system/pom.xml clean test
```

## Endpoints disponíveis

Todos os endpoints exigem autenticação HTTP Basic.

| Método | Endpoint | Descrição | Perfis permitidos |
| --- | --- | --- | --- |
| `POST` | `/customers` | Cadastrar cliente | `ADMIN` |
| `PUT` | `/customers/{id}` | Alterar cliente | `ADMIN` |
| `DELETE` | `/customers/{id}` | Excluir cliente | `ADMIN` |
| `GET` | `/customers/{id}` | Consultar cliente por ID | `USER`, `ADMIN` |
| `GET` | `/customers` | Listar clientes | `USER`, `ADMIN` |
| `GET` | `/customers?status=ACTIVE` | Filtrar clientes por status | `USER`, `ADMIN` |
| `GET` | `/customers/search?name=joao` | Buscar clientes por nome | `USER`, `ADMIN` |
| `GET` | `/customers/{id}/score` | Consultar score do cliente | `USER`, `ADMIN` |

Os status aceitos para clientes são `ACTIVE` e `INACTIVE`.

## Configurações necessárias

As configurações estão em `customer-system/src/main/resources/application.properties`:

```properties
spring.application.name=customer-system
server.port=8080

spring.datasource.url=jdbc:h2:mem:customerdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.open-in-view=false
spring.h2.console.enabled=false

app.score-service.base-url=http://localhost:8081
app.score-service.connect-timeout-seconds=2
app.score-service.read-timeout-seconds=5
app.score-service.mock=true

logging.level.com.customer.system=INFO
```

As propriedades podem ser sobrescritas por argumentos na inicialização. Exemplo:

```bash
./customer-system/mvnw -f customer-system/pom.xml spring-boot:run \
  -Dspring-boot.run.arguments="--server.port=8090"
```

## Como executar ou simular o serviço externo

Por padrão, `app.score-service.mock=true` inicia automaticamente um WireMock na porta `8081`. Nenhuma aplicação adicional precisa ser executada.

O serviço simulado atende a:

```http
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

Para utilizar um serviço externo real, desabilite o WireMock e informe a URL do serviço:

```bash
./customer-system/mvnw -f customer-system/pom.xml spring-boot:run \
  -Dspring-boot.run.arguments="--app.score-service.mock=false --app.score-service.base-url=http://localhost:9090"
```

O serviço externo deve disponibilizar `GET /scores/{cpf}` e retornar os campos `cpf`, `score` e, opcionalmente, `classification`. Falhas, indisponibilidade, timeout ou respostas inválidas são retornadas pela API como `503 Service Unavailable`.

## Exemplos de utilização da API

### Cadastrar cliente

Requer o perfil `ADMIN`.

```bash
curl -i -X POST http://localhost:8080/customers \
  -u admin:admin \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João da Silva",
    "cpf": "123.456.789-09",
    "email": "joao@email.com",
    "status": "ACTIVE"
  }'
```

### Alterar cliente

Requer o perfil `ADMIN`.

```bash
curl -i -X PUT http://localhost:8080/customers/1 \
  -u admin:admin \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João da Silva Atualizado",
    "email": "joao.atualizado@email.com",
    "status": "INACTIVE"
  }'
```

### Excluir cliente

Requer o perfil `ADMIN`.

```bash
curl -i -X DELETE http://localhost:8080/customers/1 \
  -u admin:admin
```

### Consultar cliente por ID

```bash
curl -i http://localhost:8080/customers/1 \
  -u user:user
```

### Listar clientes

```bash
curl -i http://localhost:8080/customers \
  -u user:user
```

### Filtrar clientes por status

```bash
curl -i "http://localhost:8080/customers?status=ACTIVE" \
  -u user:user
```

### Buscar clientes por nome

```bash
curl -i "http://localhost:8080/customers/search?name=joao" \
  -u user:user
```

### Consultar score do cliente

```bash
curl -i http://localhost:8080/customers/1/score \
  -u user:user
```
