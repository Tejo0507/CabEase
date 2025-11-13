# H2 → PostgreSQL migration playbook

This playbook documents safe steps to export data from a file-based H2 database and import it into PostgreSQL.

Prerequisites
- Java (JRE) available on PATH
- H2 jar placed in `lib/` (e.g. `lib/h2-2.2.220.jar`) or specify path to h2 jar
- psql CLI available and reachable PostgreSQL instance

Steps

1) Backup H2 files (non-destructive)
  - Run the included `backup-h2.ps1` script to copy H2 binaries into a timestamped folder under `backups/`.

2) Locate H2 DB files
  - Common paths in this repo:
    - `./cabease_db.mv.db`
    - `./data/cabease.mv.db`
    - `./data/cabease.trace.db`

3) Find H2 credentials
  - Search `src/main/resources/application.properties` and other config files for `spring.datasource.password` or any db password.

4) Export H2 data to SQL
  - Use the H2 Script tool to generate an SQL dump. Example (PowerShell):

    ```powershell
    # Parameters: adjust as needed
    $h2Jar = "lib\h2-2.*.jar"
    $url = "jdbc:h2:file:./data/cabease"   # or jdbc:h2:./cabease_db
    $user = "SA"
    $password = ""                         # set if DB is protected
    $output = "dump.sql"

    java -cp $h2Jar org.h2.tools.Script -url $url -user $user -password $password -script $output
    ```

  - If the H2 DB refuses the connection due to wrong password, locate the password or use a clean backup.

5) Sanitize SQL for Postgres
  - If you have a Flyway baseline (this repo includes `V1__create_schema.sql`), remove CREATE TABLE / CREATE SEQUENCE sections from the dump and keep only INSERT statements.
  - Convert H2-specific types/syntax if present (rare for simple schemas).

6) Create target DB and run baseline
  - Create database and user on Postgres side.
  - Start the app with the `prod` profile (or run Flyway) so baseline migrations create schema.

7) Import the sanitized SQL
  - Use psql to load the data: `psql -h <host> -U <user> -d cabease -f dump_sanitized.sql`

8) Fix sequences
  - For each sequence-backed table run:

    ```sql
    SELECT setval(pg_get_serial_sequence('users','id'), COALESCE(MAX(id), 1)) FROM users;
    ```

9) Validation
  - Start the application with `-Dspring.profiles.active=prod` and check logs and Actuator endpoints.

10) Rollback
  - Restore from the `backups/` folder created in step 1.

Notes
- If the H2 DB is password protected and the password is unknown, the data cannot be extracted without it.
- Do not commit raw dump files with sensitive data into version control.
