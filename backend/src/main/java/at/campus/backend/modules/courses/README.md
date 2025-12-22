# Module: courses

## Responsibility
Read-only access to course data.

## Exposed API
- GET /api/courses
- GET /api/courses/{id}

## Security
- Authentication enforced by Nginx
- Identity provided via UserContext
- No JWT handling inside module

## Data ownership
- Owns course data
- Does not modify users or reviews

## Notes
- SQL-first design
- No JPA entities
- Repository abstraction allows future persistence changes
