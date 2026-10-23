# Clube do Livro API

Diário de leitura do [Clube das Leitoras](https://clubedasleitoras.com.br), clube do livro que se encontra na Biblioteca Nacional de Brasília. A API guarda os livros do mês, a data de cada encontro e a minha avaliação de cada leitura.

## Tecnologias

- Java 21 e Spring Boot 4.1 (Spring MVC, Spring Data JPA, Bean Validation)
- SQL Server 2022 no Docker, com migrations versionadas no Flyway
- Swagger UI (springdoc-openapi)
- JUnit 5, Mockito e MockMvc

## Como rodar

Pré-requisitos: Java 21 e Docker. Ou abra no **GitHub Codespaces**, que já vem com tudo.

```bash
docker compose up -d       # sobe o SQL Server e cria o banco
./mvnw spring-boot:run     # sobe a API; o Flyway cria as tabelas e carrega os livros do clube
./mvnw test                # roda os testes
```

- Swagger: http://localhost:8080/swagger-ui.html
- Saúde: http://localhost:8080/actuator/health

## Endpoints

| Método | Rota | O que faz |
|---|---|---|
| GET | `/api/books` | Lista os livros, do encontro mais recente ao mais antigo, com a minha avaliação |
| GET | `/api/books/{id}` | Busca um livro |
| POST | `/api/books` | Cadastra um livro |
| PUT | `/api/books/{id}` | Edita um livro |
| DELETE | `/api/books/{id}` | Exclui um livro e a avaliação dele |
| GET | `/api/books/{id}/review` | Mostra a minha avaliação do livro |
| PUT | `/api/books/{id}/review` | Cria ou atualiza a minha avaliação |

## Regras de negócio

- O mesmo livro (título + autora) não entra duas vezes. O service confere antes, e um índice único no banco garante.
- Nota (1 a 5) e comentário só valem se eu li o livro, mesmo sem terminar (`READ` ou `DID_NOT_FINISH`).
- "Participei da roda" só pode ser marcado depois da data do encontro.

## Modelo de dados

- `book`: título, autora, ano de publicação, data do encontro e se a autora esteve presente
- `review`: uma por livro (a chave é o próprio id do livro), com situação da leitura, participação no encontro, nota e comentário

## Decisões

- **DTOs separados das entities:** a API nunca expõe o modelo do banco.
- **Flyway em vez de `ddl-auto=update`:** o schema muda só por migration versionada; o Hibernate apenas valida.
- **Erros no formato ProblemDetail (RFC 9457):** um `@RestControllerAdvice` converte exceções em 400, 404 e 409.
- **Sem N+1 na listagem:** livros e avaliações vêm em duas consultas, não em uma por livro.
- **Clock injetável:** a regra de data é testada com um relógio fixo e usa o fuso de Brasília.
- **NVARCHAR no SQL Server:** os títulos têm acentos.