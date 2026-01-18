# Campus++

[![CI Pipeline](https://github.com/Nexoc/campus-plus-plus/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/Nexoc/campus-plus-plus/actions/workflows/ci.yml)

Campus++ is a **containerized web application platform** developed for **Hochschule Campus Wien**.
The project demonstrates a **modern, production-oriented full-stack architecture** based on **microservices**, **JWT authentication**, and a **central NGINX gateway**.

---

## Documentation (direct links)

- **SRS (Software Requirements Specification):** [`docs/SRS.md`](docs/SRS.md)
- **MoSCoW Requirements (Functional + Non-functional):** [`docs/requirements.md`](docs/requirements.md)

> The README focuses on installation and usage. Formal requirements are maintained in the documents above.

---

## Quick Start

### Prerequisites
- Docker
- Docker Compose v2
- Free port: `80`

### 1) Configure `.env`
A root `.env` file is used for configuration. Adjust at least:
- `DB_USERNAME`, `DB_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION`
- `HOST` (used during frontend build in some host/WSL setups)

### 2) Update `HOST` automatically (Windows / WSL)
If Docker runs in **WSL** but you access the app from **Windows browser**, the frontend build may require the current Windows host IPv4.

```bash
# Usage:
chmod +x set-host-env.sh   # run once
./set-host-env.sh          # updates HOST in .env
````

### 3) Build and start

```bash
docker compose up -d --build
```

Open:

* Frontend (via gateway): `http://localhost`

### Stop / full cleanup (fresh start)

```bash
docker compose down -v
```

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

```text
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
* Built once and served as **static files** by NGINX
* No authentication logic inside the frontend
* Sends JWT in `Authorization: Bearer <token>` header

### NGINX Gateway

Acts as a **single entry point** and **security gate**.

Responsibilities:

* Serves frontend static files
* Routes requests to backend services
* Performs authentication via `auth_request`
* Blocks unauthenticated / unauthorized requests
* Forwards user identity to upstreams via headers (e.g. `X-User-Id`, `X-User-Roles`)

NGINX ensures that **no protected request reaches backend services without validation**.

### Auth Service

Spring Boot authentication service responsible for:

* User registration
* User login
* Password hashing
* JWT issuance
* JWT validation endpoint used by NGINX
* Role-based authorities
* Token versioning (server-side token revocation)
* Flyway database migrations

This service is the **only component that understands JWT structure**.

### Backend API

Main application backend (Spring Boot).

Key principles:

* Fully protected behind NGINX
* Never parses JWT directly
* Trusts headers provided by NGINX
* Focuses exclusively on business logic

Authentication is treated as an **external concern**.

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
* Spring Data JPA / Hibernate
* Flyway

### Infrastructure

* Docker / Docker Compose
* NGINX (reverse proxy + API gateway)
* PostgreSQL

---

## Authentication Flow (Detailed)

1. Client sends login request to `POST /auth/login`
2. Auth service validates credentials
3. Auth service issues JWT
4. Client stores JWT (e.g. memory / local storage)
5. Client sends requests with `Authorization: Bearer <token>`
6. NGINX:

    * Calls `/auth/validate` internally (`auth_request`)
    * Rejects request if token is invalid
    * Extracts user info from auth response
    * Forwards request to backend
7. Backend receives trusted headers:

    * `X-User-Id`
    * `X-User-Roles`

---

## Data Pipeline: Scraper vs Importer

This repository focuses on **importing** course data into the database (not scraping).

### Scraper repository (required for scraping)

Please include repo for scraper in README:

* [https://github.com/loonaarc/campuswiki_coursescraper](https://github.com/loonaarc/campuswiki_coursescraper)

> Main repo only works for importing not scraping.

---

## API Access

Everything is exposed via the NGINX gateway on port `80`.

### Frontend

* `http://localhost`

### Auth Endpoints

* `POST /auth/register`
* `POST /auth/login`
* `GET /auth/validate` (internal)

### Protected Backend

* `/api/**`

---

## Environment Profiles

Spring profiles:

* `dev`  – local development
* `test` – CI / unit tests
* `prod` – Docker / production setup

Docker uses the **prod profile by default**.

---

## Continuous Integration (CI)

GitHub Actions pipeline:

* Auth module:

    * Unit tests
    * JaCoCo test coverage
* Backend:

    * Build only
* Docker:

    * Full compose build
    * NGINX config validation

CI ensures:

* Tests must pass before Docker build
* Broken authentication logic blocks deployment
* Configuration errors are detected early

---

## Project Structure

```text
campus-plus-plus/
├── auth/                 # Auth service (Spring Boot)
├── backend/              # Main backend service
├── frontend/             # Vue frontend
├── importer/             # Importer (one-shot)
├── nginx/                # NGINX configuration
├── docs/
│   ├── SRS.md
│   ├── requirements.md
│   └── img.png
├── docker-compose.yml
├── docker-compose.dev.yml
├── .env
├── set-host-env.sh
└── README.md
```

---

## Design Notes

* Authentication is fully stateless
* Backend services never handle JWT directly
* NGINX is the single security gate
* Token revocation is handled server-side
* Database schema is versioned and reproducible

---

## Required GitHub Secrets

* `DB_HOST`
* `DB_PORT`
* `DB_NAME`
* `DB_USERNAME`
* `DB_PASSWORD`
* `JWT_SECRET`
* `JWT_EXPIRATION`

```
