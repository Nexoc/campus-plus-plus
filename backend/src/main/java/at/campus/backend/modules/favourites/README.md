
# Module: favourites

## Responsibility
Manages favourite courses for users. Allows users to add, list, and remove their favourite courses.

## Core concepts
- Favourite: A user's saved course.
- User: The owner of the favourite.
- Course: The course marked as favourite.

## API Endpoints (FavouritesController)
- `GET /api/favourites` — List all favourites for the current user (authenticated)
- `POST /api/favourites` — Add a course to favourites (authenticated)
- `DELETE /api/favourites/{courseId}` — Remove a course from favourites (authenticated)

## Ownership rules
- Favourites are owned by the user who created them.
- Users can only manage their own favourites.
- User ID is derived from UserContext (NOT from request body).

## Related modules
- courses: Favourites are linked to courses.

## Table description
**favourites**
| Column         | Type      | Description                       |
| -------------- | --------- | --------------------------------- |
| user_id        | UUID      | Owner of the favourite (PK)       |
| course_id      | UUID      | Linked course (PK)                |
| created_at     | TIMESTAMP | When the favourite was added      |

**Primary Key**: Composite key (user_id, course_id)
