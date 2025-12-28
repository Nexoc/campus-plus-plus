# Module: courses

## Responsibility
Provides access to course data, including listing, filtering, retrieving, creating, updating, and deleting courses. Handles both public and admin endpoints for course management.

## Core concepts
- Course: Represents a university course with attributes such as name, ECTS, study program, etc.
- CourseDto: Data transfer object for course data.

## API Endpoints
### Public (CoursePublicController)
- `GET /api/public/courses` — List all courses (optionally filter by `studyProgramId`, `ects`)
- `GET /api/public/courses/{id}` — Get course details by ID

### Private (CourseController)
- `POST /api/courses` — Create a new course (admin only)
- `PUT /api/courses/{id}` — Update an existing course (admin only)
- `DELETE /api/courses/{id}` — Delete a course (admin only)

## Security
- Public endpoints are accessible to all users.
- Admin endpoints require authentication and appropriate admin privileges.
- Authentication is enforced by NGINX; identity is provided via headers.
- No JWT handling inside the module itself.

## Data ownership
- Owns and manages course data.
- Does not modify user or review data.

## Table description
**courses**
| Column         | Type    | Description                       |
| -------------- | ------- | --------------------------------- |
| id             | UUID    | Primary key                       |
| name           | String  | Course name                       |
| ects           | Int     | ECTS credits                      |
| study_program  | UUID    | Linked study program              |
| ...            | ...     | Other course attributes           |

## Notes
- SQL-first design for persistence.
- No JPA entities; uses repository abstraction for future flexibility.
