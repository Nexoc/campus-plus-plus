# Module: reviews

## Responsibility
Manages user reviews for courses. Allows users to create, edit, and delete their own reviews, and view reviews for courses.

## Core concepts
- Review: User feedback for a course, including rating and comments.
- Rating: Numeric evaluation of the course.
- Difficulty, Workload, Satisfaction: Additional review metrics.
- Text: The written review content.


## API Endpoints
### Public (ReviewPublicController)
- `GET /api/public/reviews` — List all reviews (public)
- `GET /api/public/reviews/{id}` — Get review by ID (public)

### Private (ReviewController)
- `POST /api/reviews` — Create a review (authenticated users)
- `PUT /api/reviews/{id}` — Edit a review (author only)
- `DELETE /api/reviews/{id}` — Delete a review (author only)

## Ownership rules
- A review is owned by the user who created it.
- Only the author can edit or delete their review.

## Related modules
- courses: Reviews are linked to courses.
- reports: Reviews can be reported for moderation.

## Table description
**reviews**
| Column         | Type    | Description                       |
| -------------- | ------- | --------------------------------- |
| id             | UUID    | Primary key                       |
| course_id      | UUID    | Linked course                     |
| user_id        | UUID    | Author of the review              |
| rating         | Int     | Numeric rating                    |
| difficulty     | Int     | Difficulty rating                 |
| workload       | Int     | Workload rating                   |
| satisfaction   | Int     | Satisfaction rating               |
| text           | String  | Review text                       |
| created_at     | Date    | Creation timestamp                |
