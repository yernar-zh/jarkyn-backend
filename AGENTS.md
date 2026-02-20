# Backend Agent Guidelines (`jarkyn-backend`)

## Scope
- Repository: `C:\Users\Nebula\Work\jarkyn-backend`
- Stack: Spring Boot 4, Java 25, Gradle (Groovy DSL), PostgreSQL
- This file applies only to backend work in this repository.

## Project Structure
- Application code: `src/main/java/kz/jarkyn/backend/`
- Entry point: `src/main/java/kz/jarkyn/backend/BackendApplication.java`
- Resources: `src/main/resources/`
- DB migrations: `src/main/resources/db/migration/`
- Tests: `src/test/java/`

## Build and Run
- Run app: `./gradlew bootRun`
- Compile only: `./gradlew compileJava`
- Run tests: `./gradlew test`
- Full build: `./gradlew build`

## Database and Flyway
- Flyway is controlled via Gradle plugin (`org.flywaydb.flyway`), not runtime app migration dependency.
- Common commands:
  - `./gradlew flywayInfo`
  - `./gradlew flywayMigrate`
  - `./gradlew flywayValidate`
- Flyway DB settings are sourced from `src/main/resources/application-dev.yaml` in `build.gradle`.

## Coding Conventions
- Java: 4-space indentation, `UpperCamelCase` classes, `lowerCamelCase` fields/methods.
- Keep packages lowercase under `kz.jarkyn.backend`.
- Prefer constructor injection over field injection.
- Reuse existing MapStruct/Immutables patterns used in the codebase.

## Jackson and Serialization
- Project uses Jackson 3 API (`tools.jackson.*`) with Spring Boot 4.
- Do not introduce `com.fasterxml.jackson.*` imports.
- For AMQP JSON conversion, use `JacksonJsonMessageConverter` (not `Jackson2JsonMessageConverter`).

## Testing Expectations
- Add/adjust tests for behavior changes.
- Test naming: `*Test.java`.
- Keep controller/service tests focused and deterministic.

## Change Discipline
- Make minimal, targeted edits.
- Do not refactor unrelated modules during feature fixes.
- Keep secrets out of committed files.
