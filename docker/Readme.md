# Docker setup

Use Docker to run only PostgreSQL for local development, or use the project root `docker-compose.yml` for the full stack (app + database + pgAdmin).

### Create containers (database only)

```text
docker compose -f rating_infra.yml up --no-start
```

### Start

```text
docker compose -f rating_infra.yml start
```

### Stop

```text
docker compose -f rating_infra.yml stop
```

Optional: set `POSTGRES_USER`, `POSTGRES_PASSWORD`, and `POSTGRES_DB` in a `.env` file in this directory or the project root (Compose loads `.env` for variable substitution).
