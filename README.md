# Campus++

[![CI Pipeline](https://github.com/Nexoc/campus-plus-plus/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/Nexoc/campus-plus-plus/actions/workflows/ci.yml)


Campus++ is a **containerized web application platform** developed for **Hochschule Campus Wien**.
The project demonstrates a **modern, production-oriented full-stack architecture** based on **microservices**, **JWT authentication**, and a **central NGINX gateway**.

The system is designed with **security, modularity, and clear responsibility separation** in mind and serves as an educational showcase for real-world DevOps and backend/frontend integration.

---

## Key Goals of the Project

* Demonstrate **secure stateless authentication** using JWT
* Centralize security logic at the **gateway level**
* Keep backend services **authentication-agnostic**
* Apply **clean architecture principles**
* Use **Docker Compose** as orchestration layer
* Apply **CI with automated unit tests and coverage**

---

## Architecture Overview

Campus++ consists of several **isolated services**, orchestrated via **Docker Compose**.

Each service has a **single responsibility** and communicates via HTTP.

### High-Level Architecture

```
Client (Browser)
   ↓
NGINX Gateway
   ├── Frontend (static files)
   ├── Auth Service
   └── Backend API
        ↓
     PostgreSQL
```

---

## Services

### Frontend

* Vue 3 Single Page Application (SPA)
* Built once and served as **static files**
* No authentication logic inside frontend
* Sends JWT in `Authorization` header

---

### NGINX Gateway

Acts as a **single entry point** and **security gate**.

Responsibilities:

* Serves frontend static files
* Routes requests to backend services
* Performs authentication via `auth_request`
* Blocks unauthenticated or unauthorized requests
* Forwards user information via headers

NGINX ensures that **no request reaches backend services without validation**.

---

### Auth Service

Spring Boot authentication service responsible for:

* User registration
* User login
* Password hashing
* JWT issuance
* JWT validation endpoint for NGINX
* Role-based authorities
* Token versioning (server-side token revocation)
* Flyway database migrations

This service is the **only component that understands JWT structure**.

---

### Backend API

Main application backend (Spring Boot).

Key principles:

* Fully protected behind NGINX
* Never parses JWT directly
* Trusts headers provided by NGINX
* Focuses exclusively on business logic

Authentication is treated as an **external concern**.

---

### PostgreSQL

* Central relational database
* Used by auth and backend services
* Schema managed via Flyway migrations
* No manual schema manipulation

---

## Tech Stack

### Frontend

* Vue 3
* Vue Router
* Axios

### Backend

* Java 21
* Spring Boot 3
* Spring Security (stateless, JWT)
* Spring Data JPA
* Hibernate
* Flyway

### Infrastructure

* Docker
* Docker Compose
* NGINX (reverse proxy + API gateway)
* PostgreSQL

---

## Authentication Flow (Detailed)

1. Client sends login request to `/auth/login`
2. Auth service validates credentials
3. Auth service issues JWT
4. Client stores JWT (e.g. memory / local storage)
5. Client sends requests with
   `Authorization: Bearer <token>`
6. NGINX:

    * Calls `/auth/validate`
    * Rejects request if token is invalid
    * Extracts user info from response
    * Forwards request to backend
7. Backend receives trusted headers:

    * `X-User-Id`
    * `X-User-Roles`

---

## Project Structure

```
campus-plus-plus/
├── auth/            # Auth service (Spring Boot)
├── backend/         # Main backend service
├── frontend/        # Vue frontend
├── nginx/           # NGINX configuration
├── docker-compose.yml
└── README.md
```

---

## Environment Profiles

The application uses Spring profiles:

* `dev` – local development
* `test` – CI / unit tests
* `prod` – Docker / production setup

Docker uses the **prod profile by default**.

---

## Continuous Integration (CI)

The project uses **GitHub Actions** with a multi-stage pipeline:

* Auth module:

    * Unit tests
    * JaCoCo test coverage
* Backend:

    * Build only (tests planned)
* Docker:

    * Full docker-compose build
    * NGINX config validation

CI ensures:

* Tests must pass before Docker build
* Broken authentication logic blocks deployment
* Configuration errors are detected early

---

## Running the Project

### Prerequisites

* Docker
* Docker Compose v2
* Free ports: `80`, `5432`

---

### Build and Start (Clean Start)

```bash
docker compose up -d --build
```

This will:

* Build all services
* Start containers
* Run Flyway migrations automatically
* Expose the application via NGINX on port 80

---

### Verify Running Services

```bash
docker compose ps
```

Check logs (example for auth service):

```bash
docker compose logs -f auth
```

---

### Full Cleanup

```bash
docker compose down -v
```

This will:

* Stop all containers
* Remove volumes
* Delete all database data

⚠ Use only if you want a **fresh start**.

---

## API Access

### Frontend

```
http://localhost
```

### Auth Endpoints

* `POST /auth/register`
* `POST /auth/login`
* `GET /auth/validate` (internal)

### Protected Backend

```
/api/**
```

---

## Design Notes

* Authentication is fully stateless
* Backend services never handle JWT directly
* NGINX is the single security gate
* Token revocation is handled server-side
* Database schema is versioned and reproducible

## Required GitHub Secrets

- DB_HOST
- DB_PORT
- DB_NAME
- DB_USERNAME
- DB_PASSWORD
- JWT_SECRET
- JWT_EXPIRATION
