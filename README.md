# Rating service

Spring Boot service for storing and aggregating ratings.

## Prerequisites

1. JDK 17+
2. Maven 3.8+
3. Docker (optional, for the production-style stack)

## Installation

```text
mvn clean install
```

## How to run

### Profile: local

```text
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

- In-memory database: H2
- Sample data: `data.sql`
- API docs (OpenAPI UI): [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Profile: production (Docker)

For PostgreSQL, pgAdmin, and the app:

1. Copy `.env.example` to `.env` and set strong passwords.
2. Run:

```text
docker compose up --build
```

See `Dockerfile` and `docker-compose.yml`.

## How to test it

1. **GET** overall rating:

```text
http://localhost:8080/ratings?rated_entity=property_3742&specificDate=2020/11/04
```

`specificDate` is optional (`yyyy/MM/dd`). If omitted, the service uses the current date.

2. **POST** a rating:

```text
POST http://localhost:8080/ratings
Content-Type: application/json

{
  "givenRating": 5.0,
  "ratedEntity": "xe",
  "rater": null
}
```

3. Confirm:

```text
http://localhost:8080/ratings?rated_entity=xe
```

## Dependencies (high level)

- **springdoc-openapi** — OpenAPI 3 / Swagger UI
- **spring-boot-starter-actuator** — health and metrics (exposure configured per profile)
- **H2** — local profile runtime database
- **PostgreSQL** — production / Docker
- **Caffeine** — caching (`spring-boot-starter-cache`)
