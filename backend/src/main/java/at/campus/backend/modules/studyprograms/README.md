# Module: studyprograms

## Responsibility
Manages study programs and their relationship to courses. Provides endpoints for listing, retrieving, creating, updating, and deleting study programs.

## Core concepts
- StudyProgram: Represents a degree or study track.
- Name: The name of the study program.
- Description: Details about the program.

## API Endpoints
### Public (StudyProgramPublicController)
- `GET /api/public/study-programs` — List all study programs
- `GET /api/public/study-programs/{id}` — Get study program by ID
- `GET /api/public/study-programs/{id}/details` — Get detailed info for a study program

### Private (StudyProgramController)
- `POST /api/study-programs` — Create a new study program (admin only)
- `PUT /api/study-programs/{id}` — Edit a study program (admin only)
- `DELETE /api/study-programs/{id}` — Delete a study program (admin only)

## Ownership rules
- Study programs are global entities.
- Only moderators/admins can create, modify, or delete study programs.

## Related modules
- courses: Study programs are linked to courses.

## Table description
**study_programs**
| Column         | Type    | Description                       |
| -------------- | ------- | --------------------------------- |
| id             | UUID    | Primary key                       |
| name           | String  | Name of the study program         |
| description    | String  | Description of the program        |

**study_program_courses**
| Column             | Type    | Description                       |
| ------------------ | ------- | --------------------------------- |
| study_program_id   | UUID    | Linked study program              |
| course_id          | UUID    | Linked course                     |
