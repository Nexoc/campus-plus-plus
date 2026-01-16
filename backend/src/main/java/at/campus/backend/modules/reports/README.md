
# Module: reports

## Responsibility
Manages user reports for inappropriate content and moderation operations. Allows authenticated users to submit reports and moderators to review, resolve, or reject them.

## Architecture

### Role-Based Access Control

**Role Mapping:**
- Frontend: "Moderator"
- Backend: "ADMIN"

### Core Concepts

- **Report**: A user-submitted flag for inappropriate content (review, post, thread, etc.)
- **Status**: PENDING (awaiting review), RESOLVED (action taken), REJECTED (unfounded)
- **Moderator**: User with ADMIN role who manages reports

## API Endpoints

### Public Endpoints (Authenticated Users)

**POST /api/reports** — Submit a report
- Allowed roles: STUDENT, ADMIN (Moderator)
- Request body:
  ```json
  {
    "targetType": "review|post|thread",
    "targetId": "UUID of reported entity",
    "reason": "Why this content is inappropriate"
  }
  ```
- Response: `201 Created` with report details

### Moderation Endpoints (ADMIN Only)

**GET /api/moderation/reports** — List all reports
- Allowed roles: ADMIN (Moderator)
- Query parameters:
  - `status`: Optional filter (PENDING, RESOLVED, REJECTED)
- Response: List of report DTOs

**GET /api/moderation/reports/{id}** — Get specific report
- Allowed roles: ADMIN (Moderator)
- Response: Single report DTO

**PATCH /api/moderation/reports/{id}** — Update report status
- Allowed roles: ADMIN (Moderator)
- Request body:
  ```json
  {
    "status": "PENDING|RESOLVED|REJECTED",
    "moderatorNotes": "Optional notes from moderator"
  }
  ```
- Response: Updated report DTO

**DELETE /api/moderation/reports/{id}** — Delete report
- Allowed roles: ADMIN (Moderator)
- Response: `204 No Content`

## Authorization Rules

### Creating Reports
- Requires authentication (STUDENT or ADMIN)
- User ID taken from `UserContext` (never from request)
- Any authenticated user can report any content

### Moderation (All Endpoints)
- Requires ADMIN role (moderator)
- Non-moderators receive `403 Forbidden`
- Authorization enforced in service layer

## Related Modules
- **reviews**: Reports can be filed against reviews (e.g., offensive review content)
- **posts**: Reports can be filed against posts
- **threads**: Reports can be filed against forum threads

## Database Schema

**reports**
| Column           | Type      | Description                                    |
| ---------------- | --------- | ---------------------------------------------- |
| id               | UUID      | Primary key                                    |
| target_type      | String    | Type of reported entity (review, post, thread) |
| target_id        | UUID      | ID of the reported entity                      |
| user_id          | UUID      | User who submitted the report                  |
| reason           | String    | Reason for the report                          |
| status           | String    | PENDING, RESOLVED, or REJECTED                 |
| created_at       | Timestamp | When the report was created                    |
| resolved_at      | Timestamp | When the report was resolved (null if pending) |
| moderator_notes  | String    | Notes from the moderator (optional)            |

## Security Model

- **Authentication**: NGINX validates JWT and provides `X-User-Id` and `X-User-Roles`
- **Authorization**: Service layer enforces role checks via `UserContext`
- **User ID Enforcement**: Cannot be spoofed; taken from `UserContext` only
- **Audit Logging**: Moderator actions are logged with moderator ID and action details

## Response Codes

| Code | Scenario                                    |
| ---- | ------------------------------------------- |
| 201  | Report created successfully                |
| 200  | GET or PATCH successful                    |
| 204  | DELETE successful                          |
| 400  | Validation error (missing required fields) |
| 401  | Authentication required                    |
| 403  | Authorization failed (not moderator)       |
| 404  | Report not found                           |
| 500  | Server error                               |
