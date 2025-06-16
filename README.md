# PartnerHub

Backend API built with Spring Boot 3.5 and Java 21 for managing users and their external projects.

## 📚 Table of Contents

- [🚀 Tech Stack](#-tech-stack)
- [📋 Requirements](#-requirements)
  - [Minimal requirements](#-minimal-requirements)
  - [Optional extras](#-optional-extras)
- [📦 Features](#-features)
- [🌍 Environments](#-environments)
  - [Development (Default)](#️-development-default)
  - [Production](#-production)
- [🐳 Running locally](#-running-locally)
  - [Requirements](#requirements)
  - [Development Environment](#development-environment-default)
  - [Production Environment](#production-environment)
- [📡 Service Access](#-service-access)
  - [Development Environment](#development-environment)
  - [Production Environment](#production-environment-1)
- [🔐 Authentication](#-authentication)
  - [Development Credentials](#development-credentials)
  - [Production Credentials](#production-credentials)
- [🧪 Testing](#-testing)
- [📊 Observability](#-observability)
- [🧾 ER Model](#-er-model)
- [🛠 Cleanup](#-cleanup)
- [📄 License](#-license)

---

## 🚀 Tech Stack

- Java 21 + Spring Boot 3.5
- Spring Data JPA + PostgreSQL
- RESTful API with OpenAPI 3 (Swagger UI)
- MapStruct for DTO mapping
- JUnit 5 for unit/integration testing
- Docker + Docker Compose + Testcontainers
- Micrometer + Prometheus + Grafana
- Logback + JSON structured logs

---

## 📋 Requirements

### ✅ Minimal requirements

- [x] Application should have at least basic auth
- [x] Create a new user
- [x] Retrieve user information
- [x] Delete a user
- [x] Add external project to a user
- [x] Retrieve external projects from a user
- [x] Write unit tests to ensure the correctness of the controller and service logic
- [x] Containerize project using docker

### ✅ Optional extras

- [x] Update user information
- [x] Configure logs
- [x] Configure metrics
- [x] Configure docker compose with database and service and all necessary ports to be tested
- [x] Grafana dashboard
- [x] Validations
- [x] Production environment configuration
- [x] Database migrations with Flyway

---

## 📦 Features

- [x] Basic Authentication (HTTP Basic)
- [x] Create a new user (`POST /api/users`)
- [x] Retrieve user information (`GET /api/users/{id}`)
- [x] Delete a user (`DELETE /api/users/{id}`)
- [x] Update a user (`PUT /api/users/{id}`)
- [x] Add external project to user (`POST /api/users/{id}/projects`)
- [x] Retrieve external projects from a user (`GET /api/users/{id}/projects`)
- [x] Unit & integration tests
- [x] Dockerized application
- [x] JSON-structured logging with Logback
- [x] Prometheus metrics via Spring Boot Actuator
- [x] Grafana dashboard with preconfigured panels for:
  - HTTP requests per endpoint (real-time count)
  - Average response time per URI (latency)
  - JVM heap memory usage (MB)
  - Total live threads in the application
  - Errors by endpoint (4xx and 5xx)
  - Success rate (% of requests with status 2xx)
- [x] Docker Compose with PostgreSQL, Prometheus and Grafana
- [x] Environment separation (DEV/PROD) with proper configuration
- [x] Database migrations with Flyway for production deployments

---

## 🌍 Environments

This project supports separate development and production environments:

### 🛠️ Development (Default)
- **Easy setup**: Just `docker-compose up`
- **Hardcoded credentials**: Safe dummy values
- **All ports exposed**: Easy debugging and testing
- **Permissive CORS**: Frontend development friendly
- **Verbose logging**: Debug information available

### 🚀 Production
- **Environment variables**: Secure credential management
- **Restricted access**: Minimal port exposure
- **Secure CORS**: Only allowed domains
- **Optimized logging**: Essential information only
- **Health monitoring**: Production-ready observability

---

## 🐳 Running locally

### Requirements

- Java 21
- Docker & Docker Compose

### Development Environment (Default)

#### Start development environment
```bash
docker-compose up -d --build
```

### Production Environment

#### 1. Setup environment variables

```bash
cp .env.example .env
# Edit .env with your production values
```

#### 2. Build production image

```bash
docker build -t partnerhub:latest .
```

#### 3. Start production environment

```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml --env-file .env up -d
```

> 📖 **For detailed production setup**, see [PRODUCTION.md](PRODUCTION.md)

---

## 📡 Service Access

### Development Environment
- 📦 **API**: http://localhost:8080
- 📖 **Swagger UI**: http://localhost:8081
- 📊 **Grafana**: http://localhost:3000 (admin/admin)
- 📈 **Prometheus**: http://localhost:9090
- 🗄️ **PostgreSQL**: localhost:5432 (dev_user/dev_pass)

### Production Environment
- 📦 **API**: http://localhost:8080
- 📊 **Grafana**: http://localhost:3000 (admin/your_password)
- 📈 **Prometheus**: http://localhost:9090
- 📖 **Swagger UI**: Not exposed (security)
- 🗄️ **PostgreSQL**: Not exposed (security)

---

## 🔐 Authentication

All endpoints are protected with Basic Auth.

### Development Credentials

```
username: admin
password: admin
```

### Production Credentials

```
Configure in `.env` file:
ADMIN_USER=admin
ADMIN_PASS=your_secure_password_here
```

---

## 🧪 Testing

### Run tests

```bash
./gradlew test
```

### Generate coverage report

```bash
./gradlew jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

### Load testing with k6

```bash
docker-compose -f load-testing/docker-compose.k6.yml up
```

---

## 🛠️ Integration Testing with Testcontainers

This project uses Testcontainers and PostgreSQL for robust integration testing.

> [!NOTE]
> On environments using Colima, Podman, or certain CI setups, the Ryuk container reaper 
> (see: https://www.testcontainers.org/features/ryuk/) may have compatibility issues or can cause mysterious 
> "connection refused" errors. Therefore, Ryuk is disabled in all test tasks by default.

You do NOT need to manually export any environment variable — it's managed in the Gradle build:

```groovy
tasks.withType(Test).configureEach {
    environment "TESTCONTAINERS_RYUK_DISABLED", "true"
}
```

See the Ryuk documentation for more details: https://www.testcontainers.org/features/ryuk/

By default, all integration tests (those using `@Tag("integration")`) are INCLUDED in the standard test run.

No special configuration is needed in your Gradle build, just use:

```groovy
tasks.named('test') {
    useJUnitPlatform()
}
```

If you want to EXCLUDE integration tests (those with `@Tag("integration")`), you can run:

```bash
./gradlew test -Djunit.jupiter.tags='!integration'
```

Alternatively, to always exclude integration tests by default, add the following to your build.gradle:

```groovy
tasks.named('test') {
    useJUnitPlatform {
        excludeTags 'integration'
    }
}
```

To run only the integration tests, use:

```bash
./gradlew test -Djunit.jupiter.tags=integration
```

If you experience containers not being cleaned up, stop all:

```bash
docker rm -f $(docker ps -aq) # (You can remove leftover containers as needed)
```

---

## 📊 Observability

The application is fully observable:

- `/actuator/health` – health checks
- `/actuator/metrics` – Prometheus metrics
- Logs: structured JSON via LogstashEncoder (works with Loki)

Grafana dashboard auto-import available on first run.

---

## 🧾 ER Model

<p align="center">
  <img src="docs/erm.svg" alt="ER Diagram" width="500"/>
</p>
<p align="center">
  <a href="docs/erm.plantuml">PlantUML source code</a>
</p>

```sql
CREATE TABLE tb_user (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    password VARCHAR(129) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE tb_user_external_project (
    id VARCHAR(200) NOT NULL,
    user_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    description TEXT,
    url VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id, user_id),
    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
        REFERENCES tb_user(id)
        ON DELETE CASCADE
);
```

---

## 🗄️ Database Migrations

Production-ready database versioning with Flyway:

### Features
- ✅ **Version control** for database schema
- ✅ **Automated migrations** on application startup
- ✅ **Rollback support** and migration history
- ✅ **Environment separation** (DEV uses ddl-auto, PROD uses migrations)

### Migration Files

- [V1__Create_initial_tables.sql](src/main/resources/db/migration/V1__Create_initial_tables.sql)
- [V2__Add_indexes.sql](src/main/resources/db/migration/V2__Add_indexes.sql)

### Configuration

- **Development**: Uses `spring.jpa.hibernate.ddl-auto=update` for convenience
- **Production**: Uses Flyway migrations for controlled schema evolution

### Adding New Migrations

#### Create new migration file

```bash
touch src/main/resources/db/migration/V3__Update_user_columns.sql
```

Flyway will automatically apply on next startup.

---

## 🛠 Cleanup

### Stop development

```bash
docker-compose down
```

### Stop production

```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml down
```

### Remove volumes (clean slate)

```bash
docker-compose down -v # development
# or
docker-compose -f docker-compose.yml -f docker-compose.prod.yml down -v # production
```

---

## 📄 License

[MIT](LICENSE "License") – Feel free to use, fork, or improve.