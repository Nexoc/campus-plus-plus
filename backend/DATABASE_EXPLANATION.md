# Database Explanation — Campus++ Backend

## 1. Purpose of This Document

This document explains the database design of the Campus++ backend service.
It serves as a helper document to understand how the relational database
schema corresponds to the UML/domain model and architectural decisions.

The focus is on clarity, normalization and responsibility boundaries,
not on ORM or implementation details.

---

## 2. General Database Principles

The database follows these core principles:

- PostgreSQL is used as the relational database
- Flyway migrations are the single source of truth
- SQL schema is defined explicitly (no auto-generation)
- UUIDs are used as primary keys
- The backend database is independent from the Auth Service
- User data is not stored locally (only user_id references)

---

## 3. Separation from Auth Service

Although the UML model contains a `User` class, the backend database does
**not** contain a `users` table.

Reason:
- Authentication and user management are handled by a separate Auth Service
- The backend only stores references to users using `user_id (UUID)`
- This avoids tight coupling between services

As a result:
- No foreign keys reference a users table
- User identity is treated as external input

---

## 4. Database Schema Overview

All backend tables are stored inside a dedicated schema:

```

app

```

This schema isolates backend data and avoids name conflicts.

---

## 5. Table Overview and Responsibilities

### 5.1 study_programs

Stores study programs offered by the campus.

- One study program can contain many courses
- A course can belong to multiple study programs

Tables:
- `study_programs`
- `study_program_courses` (join table)

---

### 5.2 courses

Stores courses offered by the campus.

Courses are global entities and form the core of the system.
They are referenced by most other tables.

Key characteristics:
- No ownership by users
- Referenced by reviews, threads and favourites

---

### 5.3 reviews

Stores reviews written by users for courses.

Each review:
- belongs to exactly one course
- is authored by exactly one user (via user_id)

User ownership is enforced at the application level, not by database constraints.

---

### 5.4 threads

Stores discussion threads related to courses.

Each thread:
- belongs to one course
- acts as a container for posts

---

### 5.5 posts

Stores posts inside discussion threads.

Each post:
- belongs to one thread
- is authored by one user
- can be reported

---

### 5.6 favourites

Stores user favourite courses.

This is a pure relation table:
- one user can favourite many courses
- one course can be favourited by many users

The composite primary key prevents duplicate favourites.

---

### 5.7 reports

Stores reports created by users to flag inappropriate content.

Reports use a polymorphic reference:
- `target_type` defines what is being reported
- `target_id` references the reported entity

This avoids multiple report tables and keeps the schema flexible.

---

### 5.8 course_suggestions

Stores course suggestions submitted by users.

Each suggestion:
- is created by a user
- has a status lifecycle (PENDING, APPROVED, REJECTED)

Moderation is handled at the application level.

---

## 6. Enum Handling Strategy

PostgreSQL ENUM types are intentionally avoided.

Instead:
- VARCHAR columns are used
- CHECK constraints enforce valid values

Reasons:
- easier migrations
- safer schema evolution
- no enum locking during changes

---

## 7. Foreign Keys Strategy

Foreign keys are used **only** for internal domain relations:

- courses ↔ study_programs
- threads → courses
- posts → threads
- reviews → courses

No foreign keys reference external services (e.g. users).

---

## 8. Auditing Columns

Most tables contain:
- `created_at`
- `updated_at`

These fields support:
- sorting
- auditing
- future analytics

---

## 9. Why This Design Was Chosen

This database design was chosen to:

- match the UML/domain model logically
- avoid service coupling
- keep the schema normalized
- support long-term maintainability
- allow future extensions without breaking existing data

The database is treated as a stable contract rather than a side effect
of application code.

---

## 10. Current Scope

At the current stage:
- the schema is finalized
- migrations define the full structure
- no ORM mapping is required yet

This allows safe progression to API and business logic layers.


