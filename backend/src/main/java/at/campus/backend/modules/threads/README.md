# Module: threads

## Responsibility
Manages discussion threads related to courses. Provides endpoints for creating, listing, and deleting threads. Threads are used to organize discussions for specific courses.

## Core concepts
- Thread: A discussion topic related to a course.
- Title: The subject of the thread.
- Creation date: When the thread was created.


## API Endpoints
### Public (ThreadPublicController)
- `GET /api/public/threads` — List all threads (public)

### Private (ThreadController)
- `POST /api/threads` — Create a new thread (authenticated users)
- `DELETE /api/threads/{id}` — Remove a thread (moderator only)

## Ownership rules
- Threads can be created by any authenticated user.
- Only moderators may remove threads.

## Related modules
- courses: Threads are linked to courses.
- posts: Threads contain posts.
- reports: Threads can be reported for moderation.

## Table description
**threads**
| Column         | Type    | Description                       |
| -------------- | ------- | --------------------------------- |
| id             | UUID    | Primary key                       |
| title          | String  | Thread title                      |
| course_id      | UUID    | Linked course                     |
| created_by     | UUID    | User who created the thread       |
| created_at     | Date    | Creation timestamp                |
