# Campus++

Campus++ is a containerized web application platform for Hochschule Campus Wien.
It is designed as a modular, secure microservice-based system with a central gateway
and JWT-based authentication.

The project demonstrates modern full-stack architecture using Docker, Spring Boot,
Vue, NGINX, and PostgreSQL.

---

## Architecture Overview

Campus++ consists of several isolated services orchestrated via Docker Compose:

- **Frontend**  
  Vue.js Single Page Application (SPA), served as static files via NGINX.

- **NGINX Gateway**  
  Acts as a single entry point:
    - Serves the frontend
    - Routes requests to backend services
    - Performs authentication using `auth_request`
    - Blocks unauthorized access before requests reach backend services

- **Auth Service**  
  Spring Boot authentication service:
    - User registration
    - User login
    - JWT issuance
    - JWT validation endpoint for NGINX
    - Role-based authorities
    - Flyway database migrations

- **Backend API**  
  Main application backend (Spring Boot):
    - Protected behind NGINX
    - Receives authenticated user info via headers
    - Does not handle authentication directly

- **PostgreSQL**  
  Central relational database used by services (auth, backend).

---

## Tech Stack

### Frontend
- Vue 3
- Vue Router
- Axios

### Backend
- Java 21
- Spring Boot 3
- Spring Security (JWT, stateless)
- Spring Data JPA
- Hibernate
- Flyway (database migrations)

### Infrastructure
- Docker
- Docker Compose
- NGINX (reverse proxy + API gateway)
- PostgreSQL

---

## Authentication Flow (High Level)

1. Client sends login request to `/auth/login`
2. Auth service validates credentials
3. Auth service returns JWT
4. Client sends requests with `Authorization: Bearer <token>`
5. NGINX:
    - Calls `/auth/validate`
    - Blocks request if token is invalid
    - Forwards request to backend if token is valid
6. Backend receives:
    - `X-User-Id`
    - `X-User-Roles`

---

## Project Structure
campus-plus-plus/
- auth/ # Auth service (Spring Boot)
- backend/ # Main backend service
- frontend/ # Vue frontend
- nginx/ # NGINX configuration
- docker-compose.yml
- README.md

---

## Environment Profiles

The application is designed to run with profiles:

- `dev` – local development
- `prod` – Docker / production setup

Docker uses the `prod` profile by default.

---

## Running the Project

### Prerequisites

- Docker
- Docker Compose (v2)
- Ports `80`, `5432` must be free

---

### Build and Start (Clean Start)

```bash
docker compose up -d --build
```

This will:

Build all services
Start containers
Run Flyway migrations automatically
Expose the application via NGINX on port 80

### Verify Running Services
```bash
docker compose ps
```

Check logs (example for auth service):
```bash
docker compose logs -f auth
```

Full Cleanup
```bash
docker compose down -v
```
this will:
- Stop all containers
- Remove volumes
- Delete all database data
- Use only if you want a fresh start.

API Access

- Frontend:
http://localhost

- Auth endpoints:

POST /auth/register

POST /auth/login

GET /auth/validate (internal)

- Protected backend:

/api/**



### Notes
- Authentication is stateless (JWT)
- Backend services do not perform authentication themselves
- NGINX is the single security gate
- Database schema is versioned via Flyway
