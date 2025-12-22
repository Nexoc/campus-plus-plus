# Architecture Overview — Campus++ Backend

## 1. Purpose

This document describes the architectural decisions and structural
principles of the Campus++ backend service.

The goal of the architecture is to provide a clear separation of
responsibilities, reflect the domain model directly in the codebase,
and support long-term maintainability and scalability.

---

## 2. High-Level Architecture

The Campus++ system follows a multi-service architecture:

- Frontend: Vue 3 SPA
- Auth Service: Spring Boot (authentication and authorization)
- Backend Service: Spring Boot (domain logic and data)
- Gateway: Nginx (single entry point, security enforcement)
- Database: PostgreSQL
- Orchestration: Docker Compose

The backend service is **not directly exposed** to clients.
All requests pass through the gateway, which performs authentication
and forwards trusted identity information via HTTP headers.

---

## 3. Trust and Security Model

The backend service does not perform authentication.

- JWT validation is handled exclusively by the Auth Service
- The gateway validates requests using an internal auth endpoint
- The backend trusts the gateway and consumes identity information
  from request headers (e.g. user ID, roles)

This model ensures:
- no duplicated authentication logic
- reduced attack surface
- strict separation of concerns

---

## 4. Domain-Oriented Project Structure

The backend does **not** use a traditional layer-based structure
(controller/service/repository at the root level).

Instead, it follows a **domain-oriented (module-first) structure**.

```

modules/
├── courses
├── studyprograms
├── reviews
├── threads
├── posts
├── favourites
├── reports
└── coursesuggestions

```

Each module represents a distinct business domain and is responsible
for its own API, business rules and data.

### Advantages of this approach

- The folder structure directly reflects the domain model (UML)
- Business logic is not scattered across unrelated packages
- Each module has clear ownership and boundaries
- Modules can be evolved or extracted independently
- The risk of creating large “god services” is reduced

---

## 5. Module Internal Structure

Inside each domain module, responsibilities are separated as follows:

```

<module>/
├── api/        # REST controllers (HTTP layer)
├── service/    # Business logic and use cases
├── model/      # Domain models and DTOs
└── README.md   # Module contract and responsibility

```

- `api`: Handles HTTP requests and responses only
- `service`: Contains domain rules and authorization checks
- `model`: Represents domain concepts and API data structures
- `README.md`: Documents the module’s responsibility and boundaries

---

## 6. Cross-Cutting Concerns

Logic that is shared across multiple modules is placed outside of
the domain modules:

```

config/     # Application-wide configuration
security/   # Gateway-trusted user context handling
common/     # Exceptions and shared utilities

```

This prevents tight coupling between domain modules and shared logic.

---

## 7. Database Design Principles

- Database schema is defined explicitly using Flyway migrations
- SQL is treated as the primary source of truth
- ORM usage (e.g. JPA) is optional and secondary
- No foreign keys to external services (e.g. Auth Service)
- User references are stored as UUIDs only
- Production environments use schema validation, not auto-generation

---

## 8. Current Project State

At the current stage, the project focuses on:

- architectural structure
- domain boundaries
- database contracts

No business logic or persistence implementations are finalized yet.
This allows architectural decisions to be reviewed and adjusted
before implementation begins.

---

## 9. Design Goals Summary

The architecture is designed to achieve:

- clear separation of concerns
- strong alignment with the domain model
- security through gateway enforcement
- maintainability over rapid short-term development
- readiness for future scaling and extension
```

