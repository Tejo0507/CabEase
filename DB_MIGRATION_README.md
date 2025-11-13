CabEase Database Migration - H2 -> PostgreSQL
==========================================

Goal
----
Upgrade the CabEase development H2 database to a production-grade PostgreSQL database with Flyway migrations, HikariCP tuning, and Actuator health metrics — without changing UI or features.

High-level plan
----------------
1. Add production dependencies (postgresql, flyway, actuator).
2. Create Flyway baseline migration that creates the current schema.
3. Export existing H2 data (SQL dump) and import into PostgreSQL.
4. Start the app with `-Dspring.profiles.active=prod` to run Flyway migrate and validate schema.
5. Verify application functionality and run integration tests.

Prerequisites
-------------
- A running PostgreSQL server (v12+ recommended).
- psql CLI or a GUI (pgAdmin) to import data.
- Backup of existing H2 database files (usually under target/ or configured path).

Step-by-step migration
----------------------

1) Prepare PostgreSQL

  - Create database and user:

    psql -U postgres -c "CREATE DATABASE cabease;"
    psql -U postgres -c "CREATE USER cabease_user WITH PASSWORD 'some_strong_password';"
    psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE cabease TO cabease_user;"

2) Export H2 data

  - Using the H2 shell or JDBC tool, dump data to SQL. Example (run from project root):

    # Run the H2 shell export (adjust path if necessary)
    java -cp lib/h2-2.*.jar org.h2.tools.Script -url jdbc:h2:./target/h2db -user sa -script dump.sql

  - Alternatively, use the H2 Console web UI to export SQL.

3) Sanitize & adapt dump.sql for Postgres

  - H2 and Postgres differ in types and SERIAL behavior. Use the provided Flyway V1 migration as the schema baseline.
  - Remove DDL statements that conflict with Flyway baseline table creation (or adjust ordering).
  - Convert AUTO_INCREMENT to BIGSERIAL where necessary, or rely on Flyway to create tables and then INSERT data with IDs.

4) Import data into Postgres

  - First run the application once against Postgres with an empty DB and Flyway enabled to apply V1 schema:

    mvn -DskipTests -Dspring.profiles.active=prod spring-boot:run

  - Then import data using psql (adjust for sanitized dump):

    psql -U cabease_user -d cabease -f sanitized_dump.sql

  - If inserting IDs manually, ensure sequences are set correctly after import, e.g.:

    SELECT setval(pg_get_serial_sequence('users','id'), COALESCE(MAX(id),1)) FROM users;

5) Verify and switch

  - Start the application with the `prod` profile and point it to the Postgres credentials in `application-prod.properties`.
  - Run a smoke test: login, create a booking, cancel a booking, and verify emails and notifications.

Rollback plan
-------------
- Keep a copy of the original H2 dump and Postgres DB dump (pg_dump).
- If the migration causes issues, stop the application, restore Postgres from the pg_dump backup, or switch the app back to using the H2 URL and restart.

Notes & Next steps
------------------
- Consider enabling PostGIS for geospatial queries and use geometry POINT for efficient radius and nearest-driver searches.
- Add periodic vacuum/analyze and connection pool sizing based on production load and overall JVM memory limits.
- Consider moving secrets (DB credentials) to environment variables or a secrets manager and not storing them in properties files.
