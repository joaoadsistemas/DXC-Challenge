# Desafio Técnico — Desenvolvedor Java

## Objetivo

Utilizando **Java 17 ou superior**, **Spring Boot** e **Maven**, desenvolver um microsserviço responsável pelo gerenciamento de clientes.

A aplicação deverá permitir cadastrar, alterar, excluir e consultar clientes por meio de APIs REST. Além do gerenciamento dos clientes, a aplicação deverá realizar uma integração HTTP com um serviço externo responsável por fornecer informações de score do cliente.

## Dados do cliente

O cliente deverá possuir, no mínimo, as seguintes informações:

```json
{
  "id": 1,
  "name": "João da Silva",
  "cpf": "12345678901",
  "email": "joao@email.com",
  "status": "ACTIVE"
}
```

Caso considere necessário, outros atributos poderão ser adicionados.

## APIs de clientes

Implementar, no mínimo, os seguintes endpoints:

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `POST` | `/customers` | Cadastrar um cliente |
| `PUT` | `/customers/{id}` | Alterar um cliente |
| `DELETE` | `/customers/{id}` | Excluir um cliente |
| `GET` | `/customers/{id}` | Consultar um cliente por ID |
| `GET` | `/customers` | Consultar clientes |
| `GET` | `/customers/search?name=joao` | Consultar clientes por nome |
| `GET` | `/customers?status=ACTIVE` | Consultar clientes por status |

## Consulta de score

Implementar o endpoint:

```http
GET /customers/{id}/score
```

Para responder à requisição, o microsserviço deverá obter o CPF do cliente cadastrado e consultar um serviço externo.

O serviço externo possuirá uma operação equivalente a:

```http
GET /scores/{cpf}
```

Exemplo de resposta:

```json
{
  "cpf": "12345678901",
  "score": 750,
  "classification": "LOW_RISK"
}
```

Não é necessário desenvolver uma aplicação completa para representar o serviço de score. O candidato poderá utilizar **WireMock**, **MockServer**, outra aplicação Spring Boot ou outra solução que considere adequada para simular essa integração.

A aplicação deverá possuir um comportamento adequado caso ocorram problemas durante a comunicação com o serviço externo.

## Requisitos

A solução deverá utilizar:

- Java 17 ou superior;
- Spring Boot;
- Maven;
- banco de dados H2 em memória;
- Spring Data JPA;
- `JdbcTemplate`;
- pelo menos uma consulta utilizando Native Query;
- Spring Security;
- Basic Authentication;
- validação dos dados recebidos;
- tratamento de erros;
- integração HTTP com serviço externo;
- configurações da aplicação externalizadas;
- JUnit para implementação dos testes automatizados.

## Segurança

As APIs deverão estar protegidas utilizando Spring Security com Basic Authentication.

Deverão existir pelo menos dois perfis:

| Perfil | Permissões |
| --- | --- |
| `USER` | Acesso às operações de consulta |
| `ADMIN` | Acesso às operações de criação, alteração e exclusão |

As operações de criação, alteração e exclusão deverão ser permitidas apenas para usuários com a permissão apropriada.

Não é necessário desenvolver uma aplicação web para autenticação. As APIs poderão ser utilizadas por meio do Postman, Insomnia ou ferramenta equivalente.

## Tratamento das operações

Devem ser consideradas situações como:

- dados obrigatórios não preenchidos;
- CPF já cadastrado;
- cliente inexistente;
- alteração ou exclusão de cliente inexistente;
- usuário não autenticado;
- usuário sem autorização;
- indisponibilidade do serviço externo;
- falha durante a comunicação com o serviço externo;
- demora excessiva na resposta do serviço externo;
- resposta inesperada recebida do serviço externo.

As respostas HTTP deverão ser compatíveis com cada situação.

## Testes

Implementar testes automatizados para as principais funcionalidades da aplicação, incluindo regras de negócio e integração com os demais componentes da solução.

Não é necessário atingir um percentual específico de cobertura.

## Execução

A aplicação deverá poder ser executada localmente, e suas APIs poderão ser testadas utilizando Postman, Insomnia ou ferramenta equivalente.

## Entrega

Ao finalizar, disponibilizar o projeto em um repositório Git acessível aos entrevistadores.

O projeto deverá possuir um arquivo `README.md` contendo, no mínimo:

- requisitos para execução;
- como iniciar a aplicação;
- como executar os testes;
- endpoints disponíveis;
- configurações necessárias;
- como executar ou simular o serviço externo;
- exemplos de utilização da API.

---

# Implementação

Microsserviço construído com **Java 25** e **Spring Boot 4.1.1**.

A arquitetura segue os princípios de **Domain-Driven Design (DDD)** e **Arquitetura Hexagonal**, com separação entre:

- **Domain**: entidades, value objects, regras de negócio e ports (contratos de entrada/saída).
- **Application**: orquestração dos casos de uso (services).
- **Infrastructure**: adapters de entrada (REST, segurança) e saída (persistência JPA/JDBC, integração HTTP).

## Requisitos para execução

- Java 25
- Maven 3.9+
- Serviço externo de score simulado automaticamente via WireMock em `http://localhost:8081/scores/{cpf}` (pode ser desabilitado em `application.properties`)

## Como iniciar a aplicação

```bash
cd customer-system
./mvnw spring-boot:run
```

A aplicação iniciará na porta `8080`.

## Como executar os testes

```bash
cd customer-system
./mvnw test
```

## Endpoints disponíveis

Todas as APIs são protegidas por **Basic Authentication**.

| Método | Endpoint | Descrição | Perfil |
| --- | --- | --- | --- |
| `POST` | `/customers` | Cadastrar um cliente | `ADMIN` |
| `PUT` | `/customers/{id}` | Alterar um cliente | `ADMIN` |
| `DELETE` | `/customers/{id}` | Excluir um cliente | `ADMIN` |
| `GET` | `/customers/{id}` | Consultar um cliente por ID | `USER` / `ADMIN` |
| `GET` | `/customers` | Listar clientes (opcional: `?status=ACTIVE`) | `USER` / `ADMIN` |
| `GET` | `/customers/search?name=joao` | Buscar clientes por nome | `USER` / `ADMIN` |
| `GET` | `/customers/{id}/score` | Consultar score do cliente | `USER` / `ADMIN` |

Usuários padrão (em memória):

| Usuário | Senha | Perfil |
| --- | --- | --- |
| `user` | `user` | `USER` |
| `admin` | `admin` | `ADMIN` |

## Configurações necessárias

As configurações estão externalizadas em `customer-system/src/main/resources/application.properties`:

```properties
# Banco de dados H2 em memória
spring.datasource.url=jdbc:h2:mem:customerdb

# Serviço externo de score
app.score-service.base-url=http://localhost:8081
app.score-service.connect-timeout-seconds=2
app.score-service.read-timeout-seconds=5
```

## Como executar ou simular o serviço externo

Por padrão, a aplicação inicia um **mock server WireMock** na porta `8081` que responde em `GET /scores/{cpf}`. Portanto, nenhum serviço externo real é necessário para testar localmente.

Para desabilitar o mock e apontar para um serviço real, altere `application.properties`:

```properties
app.score-service.mock=false
app.score-service.base-url=http://score-servico-real:8080
```

Alternativamente, você pode simular o serviço com qualquer outra ferramenta (MockServer, outra aplicação Spring Boot, Python, etc.) que responda em `GET /scores/{cpf}`. Exemplo com Python:

```python
from http.server import BaseHTTPRequestHandler, HTTPServer
import json

class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path.startswith('/scores/'):
            cpf = self.path.split('/')[-1]
            self.send_response(200)
            self.send_header('Content-type', 'application/json')
            self.end_headers()
            self.wfile.write(json.dumps({
                "cpf": cpf,
                "score": 750,
                "classification": "LOW_RISK"
            }).encode())
        else:
            self.send_response(404)
            self.end_headers()

HTTPServer(('localhost', 8081), Handler).serve_forever()
```

## Exemplos de utilização da API

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

Resposta (`201 Created`):

```json
{
  "id": 1,
  "name": "João da Silva",
  "cpf": "123.456.789-09",
  "email": "joao@email.com",
  "status": "ACTIVE"
}
```

### Consultar cliente por ID (USER ou ADMIN)

```bash
curl -X GET http://localhost:8080/customers/1 -u user:user
```

### Consultar score (USER ou ADMIN)

```bash
curl -X GET http://localhost:8080/customers/1/score -u user:user
```

Resposta (`200 OK`):

```json
{
  "cpf": "12345678909",
  "score": 750,
  "classification": "LOW_RISK"
}
```

### Buscar clientes por nome

```bash
curl -X GET "http://localhost:8080/customers/search?name=joao" -u user:user
```

### Filtrar por status

```bash
curl -X GET "http://localhost:8080/customers?status=ACTIVE" -u user:user
```

## Tratamento de erros

A aplicação utiliza **Problem Detail (RFC 7807)** para respostas de erro:

- `400 Bad Request`: dados inválidos ou obrigatórios ausentes.
- `401 Unauthorized`: usuário não autenticado.
- `403 Forbidden`: usuário sem permissão.
- `404 Not Found`: cliente não encontrado.
- `409 Conflict`: CPF já cadastrado.
- `503 Service Unavailable`: indisponibilidade ou falha no serviço externo de score.

## Considerações sobre CAP

O serviço externo de score é um ponto de **partição / indisponibilidade** potencial. Para mitigar:

- Configuração externalizada de **timeouts** (conexão e leitura).
- Tratamento de falhas de comunicação com respostas HTTP apropriadas.
- A persistência dos clientes permanece consistente mesmo quando o serviço de score está indisponível.

## Estrutura do projeto

```
customer-system/src/main/java/com/customer/system
├── application
│   ├── dto                  # DTOs de saída dos casos de uso
│   ├── port/in              # Inbound ports (driving use cases)
│   └── service              # Implementação dos casos de uso
├── domain
│   ├── exception            # Exceções de domínio
│   ├── model                # Entidades e value objects
│   └── port/out             # Outbound ports (driven repositories/serviços)
└── infrastructure
    ├── adapter/in
    │   ├── security         # Spring Security (Basic Auth)
    │   └── web              # Controllers REST
    ├── adapter/out
    │   ├── persistence      # JPA + JDBC adapter
    │   └── score            # RestClient adapter
    └── config               # Configurações externalizadas
```
