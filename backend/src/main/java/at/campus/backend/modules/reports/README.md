
# Module: reports

## Responsibility
Manages user reports for inappropriate content. Allows users to submit reports and moderators to resolve or reject them.

## Core concepts
- Report: A user-submitted flag for inappropriate content.
- Reason: The reason for the report.
- Status: PENDING, RESOLVED, or REJECTED.


## API Endpoints (ReportPrivateController)
- `GET /api/reports` — List all reports (moderator only)
- `POST /api/reports` — Submit a report (authenticated users)
- `PUT /api/reports/{id}` — Resolve or reject a report (moderator only)

## Ownership rules
- Reports can be created by any authenticated user.
- Only moderators can resolve or reject reports.

## Related modules
- posts: Reports can be filed against posts.
- reviews: Reports can be filed against reviews.
- threads: Reports can be filed against threads.

## Table description
**reports**
| Column         | Type    | Description                       |
| -------------- | ------- | --------------------------------- |
| id             | UUID    | Primary key                       |
| target_type    | String  | Type of reported entity (post, review, thread) |
| target_id      | UUID    | ID of the reported entity         |
| user_id        | UUID    | User who submitted the report     |
| reason         | String  | Reason for the report             |
| status         | String  | PENDING, RESOLVED, or REJECTED    |
| created_at     | Date    | Creation timestamp                |
