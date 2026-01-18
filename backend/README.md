
# Campus++

[![CI Pipeline](https://github.com/Nexoc/campus-plus-plus/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/Nexoc/campus-plus-plus/actions/workflows/ci.yml)

Campus++ is a **containerized web application platform** developed for **Hochschule Campus Wien**.
The project demonstrates a modern, production-oriented full-stack architecture based on **microservices**, **JWT authentication**, and a **central NGINX gateway**.

The system is designed with **security, modularity, and clear responsibility separation** in mind and serves as an educational showcase for real-world DevOps and backend/frontend integration.

---

## Quick Start

### 1) Prerequisites

* Docker
* Docker Compose v2
* Free port: `80`

> If you want to access Postgres from your host machine (e.g. DB tooling), use the **dev compose** file (it publishes `5432`).

### 2) Configure `.env`

This repository uses a root `.env` file (checked in for convenience). Adjust values for your setup:

* `DB_USERNAME`, `DB_PASSWORD`
* `JWT_SECRET` (use a strong secret in real deployments)
* `JWT_EXPIRATION` (minutes)
* `HOST` (used during frontend build)

### 3) Update `HOST` automatically (Windows / WSL)

If you run Docker from **WSL** and your browser runs on **Windows**, the frontend build needs the current **Windows host IPv4**.
Use the helper script:

```bash
# Usage:
chmod +x set-host-env.sh   # run once
./set-host-env.sh          # updates HOST in .env
```

### 4) Start everything

```bash
docker compose up -d --build
```

Open:

* Frontend (via gateway): `http://localhost`

To stop and remove volumes (fresh start):

```bash
docker compose down -v
```

---

## Key Goals

* Demonstrate **secure stateless authentication** using JWT
* Centralize security logic at the **gateway level**
* Keep backend services **authentication-agnostic**
* Apply **clean architecture** principles
* Use **Docker Compose** as orchestration layer
* Apply **CI** with automated unit tests and coverage

---

## Usage (User Guide)

### Roles

- **Guest** or **Applicant**: can browse study programs, courses, and read reviews.
- **Student**: can write/edit own reviews, manage favourites, report inappropriate reviews.
- **Moderator**: can moderate reported reviews and access moderation views.

### Typical user flows

1. Open the homepage and browse available **study programs**.
2. Select a study program to view associated **courses**.
3. Open a course detail page to see description, semester, and **reviews + average rating**.
4. Create an account and log in to:
    - add courses/programs to **favourites**
    - write a **review** (Student / Moderator)
    - report inappropriate reviews
5. As a Moderator:
    - open the moderation view
    - process reports and set status: keep visible / edit / delete


## Architecture Overview

Campus++ consists of several isolated services orchestrated via Docker Compose.
Each service has a single responsibility and communicates via HTTP.

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
* Built once and served as static files
* Sends JWT in `Authorization` header
* Uses `HOST` during build (see **HOST / WSL** section)

### NGINX Gateway

Single entry point and security gate.

Responsibilities:

* Serves frontend static files
* Routes requests to backend services
* Performs authentication via `auth_request`
* Blocks unauthenticated / unauthorized requests
* Forwards user identity via headers (`X-User-Id`, `X-User-Roles`)

### Auth Service

Spring Boot authentication service responsible for:

* User registration
* User login
* Password hashing
* JWT issuance
* JWT validation endpoint (internal, called by NGINX)
* Role-based authorities
* Token versioning (server-side token revocation)
* Flyway database migrations

This service is the only component that understands JWT structure.

### Backend API

Main application backend (Spring Boot).

Key principles:

* Fully protected behind NGINX
* Never parses JWT directly
* Trusts headers provided by NGINX
* Focuses exclusively on business logic

### PostgreSQL

* Central relational database
* Used by auth and backend services
* Schema managed via Flyway migrations
* Persistent data stored in Docker volumes

---

## Data Pipeline: Scraper vs Importer

This repository focuses on **importing** course data into the database.

* **Importer (this repo):** imports an already prepared JSON file into Postgres (one-shot container).
* **Scraper:** maintained separately.

### Scraper repository (required for scraping)

Please include repo for scraper in readme:

* [https://github.com/loonaarc/campuswiki_coursescraper](https://github.com/loonaarc/campuswiki_coursescraper)

> Main repo only works for importing not scraping.

### Importer behaviour

* Runs once at startup
* Waits until Flyway has created the schema
* If the DB already contains study programs, the importer exits (no duplicates)

### Updating the imported dataset

If you re-scrape data and want to import a new JSON:

1. Replace `importer/hcw_courses.json` with the new file.
2. Rebuild and reset the importer volume:

```bash
docker compose down -v
docker compose up -d --build
```

(Alternatively, you can run only the importer after startup:
`docker compose run --rm importer`.)

---

## API Access

Everything is exposed via the NGINX gateway on port `80`.

### Frontend

* `http://localhost`

### Auth endpoints

* `POST /auth/register`
* `POST /auth/login`

> `GET /auth/validate` is internal and blocked from direct access.

### Backend endpoints

* Public (no auth): `/api/public/**`
* Protected (JWT required): `/api/**`

---

## Authentication Flow

1. Client sends login request to `POST /auth/login`
2. Auth service validates credentials
3. Auth service issues JWT
4. Client sends requests with `Authorization: Bearer <token>`
5. NGINX calls the auth validation endpoint via `auth_request`
6. NGINX forwards identity to upstreams via:

    * `X-User-Id`
    * `X-User-Roles`
7. Backend trusts these headers and focuses on business logic

---

## Development Mode (Hot Reload Frontend)

Use `docker-compose.dev.yml` for a dev workflow with a live frontend.

```bash
docker compose -f docker-compose.dev.yml up -d --build
```

* Frontend dev server: `http://localhost:3000`
* Gateway still available on: `http://localhost` (port `80`)

After frontend changes:

```bash
docker compose -f docker-compose.dev.yml restart frontend
```

---

## Project Structure

```
campus-plus-plus/
├── auth/                 # Auth service (Spring Boot)
├── backend/              # Main backend service (Spring Boot)
├── frontend/             # Vue frontend
├── importer/             # One-shot importer (Python)
├── nginx/                # NGINX gateway configuration
├── docker-compose.yml
├── docker-compose.dev.yml
├── set-host-env.sh       # updates HOST in .env (Windows/WSL helper)
└── README.md
```

---

## Environment Profiles

Spring profiles used in the project:

* `dev`  – local development
* `test` – CI / unit tests
* `prod` – production configuration

---

## Continuous Integration (CI)

GitHub Actions pipeline (see `.github/workflows/ci.yml`):

* Auth module:

    * unit tests
    * JaCoCo coverage summary
* Backend:

    * build only
* Docker:

    * full `docker compose up -d --build`
    * NGINX config validation (`nginx -t`)

---

## Required GitHub Secrets

Used by CI:

* `DB_HOST`
* `DB_PORT`
* `DB_NAME`
* `DB_USERNAME`
* `DB_PASSWORD`
* `JWT_SECRET`
* `JWT_EXPIRATION`

---

## Troubleshooting

### Frontend cannot reach the API

* Ensure you access the app via `http://localhost` (gateway).
* If you run Docker in WSL, update `HOST` using `set-host-env.sh` before building.

### Re-import data

* `docker compose down -v` will delete volumes (including DB data) and allow a clean import.
