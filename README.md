# Clube do Livro API

Diário de leitura do [Clube das Leitoras](https://clubedasleitoras.com.br), um clube do livro que se encontra na Biblioteca Nacional de Brasília. A API guarda os livros do mês, a data de cada encontro e as minhas avaliações de cada leitura.

> Em construção.

## Tecnologias

- Java 21 e Spring Boot 4.1
- PostgreSQL 16 no Docker, com migrations no Flyway
- Swagger UI (springdoc-openapi)

## Como rodar

### No navegador, com GitHub Codespaces

Em **Code → Codespaces → Create codespace on main**. O ambiente já vem com Java 21, Maven e Docker.

### Na sua máquina

Pré-requisitos: Java 21 e Docker.

```bash
docker compose up -d       # sobe o Postgres
./mvnw spring-boot:run     # sobe a API
```

- Saúde da API: http://localhost:8080/actuator/health
- Swagger: http://localhost:8080/swagger-ui.html
