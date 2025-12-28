
## Responsibility
Manages user-submitted suggestions for new courses.

## Core concepts

## Ownership rules
Course suggestions are created by users.
Only moderators can approve or reject suggestions.

## Related modules

## Database tables
# Module: CourseSuggestions

## Responsibility
Manages user-submitted suggestions for new courses.

# Module: CourseSuggestions

## Responsibility
Manages user-submitted suggestions for new courses.

## Core concepts
- CourseSuggestion
- Status
- Submission date

## API Endpoints
- `GET /api/coursesuggestions` — List all suggestions (moderator only)
- `POST /api/coursesuggestions` — Submit a suggestion (auth required)
- `PUT /api/coursesuggestions/{id}` — Approve/reject a suggestion (moderator only)

## Ownership rules
Course suggestions are created by users.
Only moderators can approve or reject suggestions.

## Related modules
- courses

## Database tables
- course_suggestions
