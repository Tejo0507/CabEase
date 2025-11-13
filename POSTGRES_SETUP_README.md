# Postgres setup for CabEase

This project includes a production-grade PostgreSQL setup using Docker Compose and Flyway migrations.

Quick start (local)

1. Start Postgres and pgAdmin:

```powershell
# from project root
docker-compose up -d
```

2. Verify Postgres is up on port 5432 and pgAdmin on 8081.

3. Build and run the app using the `prod` profile, pointing to the local Postgres instance:

```powershell
# Example environment (adjust as needed)
$env:POSTGRES_HOST='localhost'
$env:POSTGRES_PORT='5432'
$env:POSTGRES_DB='cabease'
$env:POSTGRES_USER='cabease'
$env:POSTGRES_PASSWORD='cabease_pass'

mvn -DskipTests package
java -Dspring.profiles.active=prod -jar target\cabease-1.0.0.jar
```

Flyway

- Flyway is enabled in `application-prod.properties` and will run migrations on application startup.
- Migrations live in `src/main/resources/db/migration` and include `V1__create_schema.sql`.

Importing existing data

- If you later export H2 to SQL (see `H2_TO_POSTGRES_PLAYBOOK.md`), sanitize the dump to only include INSERTs and then import via `psql`:

```powershell
psql -h localhost -U cabease -d cabease -f dump_sanitized.sql
```

Notes

- The Docker Compose file mounts `docker/init-db.sql` to run at container initialization.
- For production deployments, use a managed Postgres service, secure credentials through environment variables or a secrets manager, and enable backups.
