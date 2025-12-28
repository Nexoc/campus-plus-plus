# Module: posts

## Responsibility
Manages posts within discussion threads. Allows users to create, edit, and delete their own posts, and view posts in threads.

## Core concepts
- Post: A message within a thread.
- Content: The text of the post.
- Author: The user who wrote the post.
- Creation date: When the post was created.


## API Endpoints
### Public (PostPublicController)
- `GET /api/public/posts` — List all posts (public)

### Private (PostController)
- `POST /api/posts` — Create a post (authenticated users)
- `PUT /api/posts/{id}` — Edit a post (author or moderator)
- `DELETE /api/posts/{id}` — Delete a post (author or moderator)

## Ownership rules
- A post is owned by its author.
- Only the author or a moderator can edit or delete a post.

## Related modules
- threads: Posts belong to threads.
- reports: Posts can be reported for moderation.

## Table description
**posts**
| Column         | Type    | Description                       |
| -------------- | ------- | --------------------------------- |
| id             | UUID    | Primary key                       |
| thread_id      | UUID    | Linked thread                     |
| user_id        | UUID    | Author of the post                |
| content        | String  | Post content                      |
| created_at     | Date    | Creation timestamp                |
