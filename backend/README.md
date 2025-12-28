# Campus++ Backend

This is the backend service for the Campus++ platform. It provides all core business logic, data management, and API endpoints for the application, following a modular, microservice-inspired architecture.

## Features
- Modular structure: each domain (courses, reviews, threads, etc.) is a separate module
- Stateless, authentication-agnostic (all authentication is handled by the Auth Service and NGINX gateway)
- RESTful API endpoints for all core entities
- PostgreSQL database with Flyway migrations
- Clean separation of concerns and responsibility boundaries

## Main Modules
- Courses
- Study Programs
- Reviews
- Threads
- Posts
- Reports
- Favourites
- Course Suggestions

## Key Files
- `ARCHITECTURE.md`: Detailed backend architecture
- `DATABASE_EXPLANATION.md`: Database schema and design principles
- `src/main/java/at/campus/backend/modules/`: All backend modules and their code

## Running Locally
This backend is designed to be run as part of the full Campus++ stack using Docker Compose. To start all services:

```
docker compose up -d --build
```

## API Documentation
See each module's README for detailed endpoint documentation and data model descriptions.

## Security Model
- All requests are routed through the NGINX gateway
- The backend trusts identity and authorization headers set by the gateway
- No JWT or user management logic is present in the backend

## Contact
For questions or contributions, see the main Campus++ repository.
