# Livraria API

API REST para sistema de gerenciamento de livraria, desenvolvida como trabalho da disciplina de Desenvolvimento Web Java.

Repositório: <https://github.com/LuizVieiraG/API-REST-JAVA>

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3.4.1
- Spring Web (REST)
- Spring Data JPA + Hibernate
- Bean Validation (jakarta.validation)
- MySQL 8
- Maven 3.9.12

## Requisitos para Executar

- Java 17 instalado
- MySQL 8 instalado e em execução
- Maven (ou usar o `mvnw.cmd` incluído no projeto)

## Configuração do Banco MySQL

1. Abra o MySQL e execute:

```sql
CREATE DATABASE IF NOT EXISTS livraria_db;
```

2. Abra o arquivo `src/main/resources/application.properties` e configure:

```properties
spring.datasource.username=root
spring.datasource.password=SUA_SENHA
```

> O banco `livraria_db` é criado automaticamente se a propriedade `createDatabaseIfNotExist=true` estiver na URL e o usuário tiver permissão.

## Como Executar o Projeto

Clone o repositório:

```bash
git clone https://github.com/LuizVieiraG/API-REST-JAVA.git
cd API-REST-JAVA
```

No terminal, dentro da pasta do projeto:

```bash
# Usando o wrapper Maven incluído no projeto
.\mvnw.cmd spring-boot:run

# Ou compilar e gerar o JAR
.\mvnw.cmd package
java -jar target/livraria-api-0.0.1-SNAPSHOT.jar
```

A API ficará disponível em: `http://localhost:8080`

---

## Como Executar os Testes

```bash
# Executa os testes de controller
.\mvnw.cmd test

# Executa testes unitários e testes de integração (*IT)
.\mvnw.cmd verify
```

---

## Endpoints Disponíveis

### Inicial

| Método | URL | Descrição |
|--------|-----|-----------|
| GET | / | Verifica se a API está em execução e lista os endpoints principais |

### Autores

| Método | URL | Descrição |
|--------|-----|-----------|
| GET | /api/autores | Listar todos os autores |
| GET | /api/autores/{id} | Buscar autor por ID |
| POST | /api/autores | Cadastrar novo autor |
| PUT | /api/autores/{id} | Atualizar autor |
| DELETE | /api/autores/{id} | Excluir autor |

### Livros

| Método | URL | Descrição |
|--------|-----|-----------|
| GET | /api/livros | Listar todos os livros |
| GET | /api/livros/{id} | Buscar livro por ID |
| POST | /api/livros | Cadastrar novo livro |
| PUT | /api/livros/{id} | Atualizar livro |
| DELETE | /api/livros/{id} | Excluir livro |

---

## Exemplos de Requisições JSON

### Verificar API

**GET** `http://localhost:8080/`

**Resposta (200 OK):**
```json
{
  "mensagem": "Livraria API em execução",
  "endpoints": [
    "/api/autores",
    "/api/livros"
  ]
}
```

---

### Cadastrar Autor

**POST** `http://localhost:8080/api/autores`

```json
{
  "nome": "Machado de Assis",
  "email": "machado@literatura.br"
}
```

**Resposta (201 Created):**
```json
{
  "id": 1,
  "nome": "Machado de Assis",
  "email": "machado@literatura.br",
  "livros": []
}
```

---

### Listar Autores

**GET** `http://localhost:8080/api/autores`

**Resposta (200 OK):**
```json
[
  {
    "id": 1,
    "nome": "Machado de Assis",
    "email": "machado@literatura.br",
    "livros": ["Dom Casmurro"]
  }
]
```

---

### Cadastrar Livro

> Atenção: cadastre pelo menos um autor antes de cadastrar um livro.

**POST** `http://localhost:8080/api/livros`

```json
{
  "titulo": "Dom Casmurro",
  "isbn": "978-85-359-0277-5",
  "preco": 39.90,
  "autorIds": [1]
}
```

**Resposta (201 Created):**
```json
{
  "id": 1,
  "titulo": "Dom Casmurro",
  "isbn": "978-85-359-0277-5",
  "preco": 39.90,
  "autores": [
    {
      "id": 1,
      "nome": "Machado de Assis",
      "email": "machado@literatura.br",
      "livros": null
    }
  ]
}
```

---

### Atualizar Autor

**PUT** `http://localhost:8080/api/autores/1`

```json
{
  "nome": "Machado de Assis (atualizado)",
  "email": "machado.novo@literatura.br"
}
```

---

### Excluir Livro

**DELETE** `http://localhost:8080/api/livros/1`

**Resposta: 204 No Content**

---

### Buscar por ID inexistente

**GET** `http://localhost:8080/api/autores/999`

**Resposta (404 Not Found):**
```json
{
  "status": 404,
  "mensagem": "Autor não encontrado com id: 999",
  "timestamp": "2026-05-14T10:00:00",
  "erros": null
}
```

---

### Exemplo de erro de validação

**POST** `http://localhost:8080/api/livros` com body inválido:
```json
{
  "titulo": "",
  "isbn": "",
  "preco": -10,
  "autorIds": []
}
```

**Resposta (400 Bad Request):**
```json
{
  "status": 400,
  "mensagem": "Erro de validação",
  "timestamp": "2026-05-14T10:00:00",
  "erros": {
    "titulo": "Título é obrigatório",
    "isbn": "ISBN é obrigatório",
    "preco": "Preço deve ser um valor positivo",
    "autorIds": "Pelo menos um autor deve ser informado"
  }
}
```

---

## Relacionamento Muitos-para-Muitos

O sistema implementa relacionamento **ManyToMany** entre `Livro` e `Autor`:

- Um livro pode ter vários autores
- Um autor pode escrever vários livros
- O relacionamento é gerenciado pela tabela intermediária `livro_autor` com colunas `livro_id` e `autor_id`

Para associar um livro a múltiplos autores, informe os IDs no campo `autorIds`:

```json
{
  "titulo": "Livro Coletivo",
  "isbn": "000-00-000-0000-0",
  "preco": 59.90,
  "autorIds": [1, 2, 3]
}
```

---

## Validações

| Campo | Regra |
|-------|-------|
| Autor: nome | Obrigatório, não vazio |
| Autor: email | Formato válido de e-mail (opcional) |
| Livro: titulo | Obrigatório, não vazio |
| Livro: isbn | Obrigatório, não vazio |
| Livro: preco | Deve ser valor positivo |
| Livro: autorIds | Deve conter pelo menos um ID válido |

---

## Tratamento de Exceções

O handler global (`@RestControllerAdvice`) trata:

| Situação | HTTP |
|----------|------|
| Recurso não encontrado | 404 Not Found |
| Dados inválidos (validação) | 400 Bad Request |
| Erro genérico | 500 Internal Server Error |

Todas as respostas de erro retornam JSON com `status`, `mensagem`, `timestamp` e `erros`.

---

## Roteiro para Apresentação

1. Tema escolhido: sistema de gerenciamento de livraria.
2. Entidades principais: `Autor` e `Livro`.
3. Relacionamento: muitos-para-muitos entre autores e livros.
4. Camadas: controller, service, repository, model, dto e exception.
5. Endpoints principais: CRUD de autores e CRUD de livros.
6. Validações: campos obrigatórios, e-mail válido, preço positivo e autores obrigatórios no livro.
7. Tratamento de exceções: respostas padronizadas para validação, recurso não encontrado e erro interno.
8. Demonstração: cadastrar autores, cadastrar livro com autores, listar, buscar por ID, atualizar, excluir e mostrar erro de validação.

---

## Estrutura do Projeto

```
livraria-api/
├── src/main/java/com/livraria/api/
│   ├── controller/       ← Recebe as requisições HTTP
│   ├── service/          ← Regras de negócio
│   ├── repository/       ← Acesso ao banco de dados (JPA)
│   ├── model/            ← Entidades JPA (Livro, Autor)
│   ├── dto/              ← Objetos de entrada e saída
│   └── exception/        ← Exceções e handler global
└── src/main/resources/
    └── application.properties
```
