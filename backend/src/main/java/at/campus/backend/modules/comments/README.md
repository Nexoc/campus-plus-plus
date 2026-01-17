# Comments Module

This module manages comments on posts in discussion threads.

## Database Schema

```sql
CREATE TABLE app.comments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id UUID NOT NULL REFERENCES app.posts(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES app.accounts(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## API Endpoints

### Public Endpoints (Read-Only)

- `GET /api/public/posts/{postId}/comments` - Get all comments for a post
- `GET /api/public/comments/{commentId}` - Get a specific comment

### Protected Endpoints (Authenticated Users)

- `POST /api/posts/{postId}/comments` - Create a new comment
- `PUT /api/comments/{commentId}` - Update a comment (author or moderator only)
- `DELETE /api/comments/{commentId}` - Delete a comment (author or moderator only)

## Model

- **Comment**: Represents a comment on a post
  - `id`: UUID - Unique identifier
  - `postId`: UUID - Reference to the post
  - `userId`: UUID - User who created the comment
  - `content`: String - Comment text
  - `createdAt`: LocalDateTime - Creation timestamp
  - `updatedAt`: LocalDateTime - Last update timestamp

## Authorization Rules

- **Create**: Authenticated users only
- **Update**: Only the author or moderators can update
- **Delete**: Only the author or moderators can delete
- **Read**: All users (authenticated and anonymous)
